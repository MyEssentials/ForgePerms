/*
 * PermissionsEx - Permissions plugin for Bukkit
 * Copyright (C) 2011 t3hk0d3 http://www.tehkode.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package pex.permissions.backends;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pex.permissions.PermissionBackend;
import pex.permissions.PermissionGroup;
import pex.permissions.PermissionManager;
import pex.permissions.PermissionUser;
import pex.permissions.backends.file.FileGroup;
import pex.permissions.backends.file.FileUser;

/**
 * 
 * @author code
 */
public class FileBackend extends PermissionBackend {

	public final static char PATH_SEPARATOR = '/';
	public FileConfiguration permissions;
	public File permissionsFile;

	public FileBackend(PermissionManager manager, net.minecraftforge.common.Configuration config) {
		super(manager, config);
	}

	@Override
	public void initialize() {
		String permissionFilename = config.get("permissions", "backends_file_file", "permissions.yml").getString();

		String baseDir = config.get("permissions", "basedir", "config").getString();

		if (baseDir.contains("\\") && !"\\".equals(File.separator)) {
			baseDir = baseDir.replace("\\", File.separator);
		}

		File baseDirectory = new File(baseDir);
		if (!baseDirectory.exists()) {
			baseDirectory.mkdirs();
		}

		permissionsFile = new File(baseDir, permissionFilename);

		reload();

		if (!permissionsFile.exists()) {
			try {
				permissionsFile.createNewFile();

				// Load default permissions
				permissions.set("groups/default/default", true);

				List<String> defaultPermissions = new LinkedList<String>();
				// Specify here default permissions
				defaultPermissions.add("modifyworld.*");

				permissions.set("groups/default/permissions", defaultPermissions);

				save();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String[] getWorldInheritance(String world) {
		if (world != null && !world.isEmpty()) {
			List<String> parentWorlds = permissions.getStringList(buildPath("worlds", world, "/inheritance"));
			if (parentWorlds != null) {
				return parentWorlds.toArray(new String[parentWorlds.size()]);
			}
		}

		return new String[0];
	}

	@Override
	public void setWorldInheritance(String world, String[] parentWorlds) {
		if (world == null || world.isEmpty()) {
			return;
		}

		permissions.set(buildPath("worlds", world, "inheritance"), Arrays.asList(parentWorlds));
		save();
	}

	@Override
	public PermissionUser getUser(String userName) {
		return new FileUser(userName, manager, this);
	}

	@Override
	public PermissionGroup getGroup(String groupName) {
		return new FileGroup(groupName, manager, this);
	}

	@Override
	public PermissionGroup getDefaultGroup(String worldName) {
		ConfigurationSection groups = permissions.getConfigurationSection("groups");

		if (groups == null) {
			throw new RuntimeException("No groups defined. Check your permissions file.");
		}

		String defaultGroupProperty = "default";
		if (worldName != null) {
			defaultGroupProperty = buildPath("worlds", worldName, defaultGroupProperty);
		}

		for (Map.Entry<String, Object> entry : groups.getValues(false).entrySet()) {
			if (entry.getValue() instanceof ConfigurationSection) {
				ConfigurationSection groupSection = (ConfigurationSection) entry.getValue();

				if (groupSection.getBoolean(defaultGroupProperty, false)) {
					return manager.getGroup(entry.getKey());
				}
			}
		}

		if (worldName == null) {
			throw new RuntimeException("Default user group is not defined. Please select one using the \"default: true\" property");
		}

		return null;
	}

	@Override
	public void setDefaultGroup(PermissionGroup group, String worldName) {
		ConfigurationSection groups = permissions.getConfigurationSection("groups");

		String defaultGroupProperty = "default";
		if (worldName != null) {
			defaultGroupProperty = buildPath("worlds", worldName, defaultGroupProperty);
		}

		for (Map.Entry<String, Object> entry : groups.getValues(false).entrySet()) {
			if (entry.getValue() instanceof ConfigurationSection) {
				ConfigurationSection groupSection = (ConfigurationSection) entry.getValue();

				groupSection.set(defaultGroupProperty, false);

				if (!groupSection.getName().equals(group.getName())) {
					groupSection.set(defaultGroupProperty, null);
				} else {
					groupSection.set(defaultGroupProperty, true);
				}
			}
		}

		save();
	}

	@Override
	public PermissionGroup[] getGroups() {
		List<PermissionGroup> groups = new LinkedList<PermissionGroup>();
		ConfigurationSection groupsSection = permissions.getConfigurationSection("groups");

		if (groupsSection == null) {
			return new PermissionGroup[0];
		}

		for (String groupName : groupsSection.getKeys(false)) {
			groups.add(manager.getGroup(groupName));
		}

		Collections.sort(groups);

		return groups.toArray(new PermissionGroup[0]);
	}

	@Override
	public PermissionUser[] getRegisteredUsers() {
		List<PermissionUser> users = new LinkedList<PermissionUser>();
		ConfigurationSection usersSection = permissions.getConfigurationSection("users");

		if (usersSection != null) {
			for (String userName : usersSection.getKeys(false)) {
				users.add(manager.getUser(userName));
			}
		}

		return users.toArray(new PermissionUser[users.size()]);
	}

	public static String buildPath(String... path) {
		StringBuilder builder = new StringBuilder();

		boolean first = true;
		char separator = PATH_SEPARATOR; // permissions.options().pathSeparator();

		for (String node : path) {
			if (!first) {
				builder.append(separator);
			}

			builder.append(node);

			first = false;
		}

		return builder.toString();
	}

	@Override
	public void reload() {
		permissions = new YamlConfiguration();
		permissions.options().pathSeparator(PATH_SEPARATOR);

		try {
			permissions.load(permissionsFile);
		} catch (FileNotFoundException e) {
			// do nothing
		} catch (Throwable e) {
			throw new IllegalStateException("Error loading permissions file", e);
		}
	}

	public void save() {
		try {
			permissions.save(permissionsFile);
		} catch (IOException e) {
			Logger.getLogger("Minecraft").severe("[PermissionsEx] Error during saving permissions file: " + e.getMessage());
		}
	}

	@Override
	public void dumpData(OutputStreamWriter writer) throws IOException {
		throw new UnsupportedOperationException("Sorry, data dumping is broken!");
	}
}
