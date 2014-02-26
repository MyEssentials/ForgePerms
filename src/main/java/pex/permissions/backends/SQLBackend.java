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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import pex.permissions.PermissionBackend;
import pex.permissions.PermissionGroup;
import pex.permissions.PermissionManager;
import pex.permissions.PermissionUser;
import pex.permissions.backends.sql.SQLConnection;
import pex.permissions.backends.sql.SQLEntity;
import pex.permissions.backends.sql.SQLGroup;
import pex.permissions.backends.sql.SQLUser;
import pex.utils.StringUtils;

/**
 * 
 * @author code
 */
public class SQLBackend extends PermissionBackend {

	protected Map<String, String[]> worldInheritanceCache = new HashMap<String, String[]>();
	public SQLConnection sql;

	public SQLBackend(PermissionManager manager, net.minecraftforge.common.Configuration config) {
		super(manager, config);
	}

	@Override
	public void initialize() {
		String dbDriver = config.get("permissions", "backends_sql_driver", "mysql").getString();
		String dbUri = config.get("permissions", "backends_sql_uri", "mysql://localhost/exampledb").getString();
		String dbUser = config.get("permissions", "backends_sql_user", "databaseuser").getString();
		String dbPassword = config.get("permissions", "backends_sql_password", "databasepassword").getString();

		sql = new SQLConnection(dbUri, dbUser, dbPassword, dbDriver);

		Logger.getLogger("Minecraft").info("[PermissionsEx-SQL] Successfuly connected to database");

		setupAliases(config);
		deployTables(dbDriver);
	}

	@Override
	public PermissionUser getUser(String name) {
		return new SQLUser(name, manager, sql);
	}

	@Override
	public PermissionGroup getGroup(String name) {
		return new SQLGroup(name, manager, sql);
	}

	@Override
	public PermissionGroup getDefaultGroup(String worldName) {
		try {
			ResultSet result;

			if (worldName == null) {
				result = sql.select("SELECT `name` FROM `permissions_entity` WHERE `type` = ? AND `default` = 1 LIMIT 1", SQLEntity.Type.GROUP.ordinal());

				if (!result.next()) {
					throw new RuntimeException("There is no default group set, this is a serious issue");
				}
			} else {
				result = sql.select("SELECT `name` FROM `permissions` WHERE `permission` = 'default' AND `value` = 'true' AND `type` = ? AND `world` = ?", SQLEntity.Type.GROUP.ordinal(), worldName);

				if (!result.next()) {
					return null;
				}
			}

			return manager.getGroup(result.getString("name"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setDefaultGroup(PermissionGroup group, String worldName) {
		try {
			if (worldName == null) {
				// Reset default flag
				sql.executeUpdate("UPDATE `permissions_entity` SET `default` = 0 WHERE `type` = ? AND `default` = 1 LIMIT 1", SQLEntity.Type.GROUP.ordinal());
				// Set default flag
				sql.executeUpdate("UPDATE `permissions_entity` SET `default` = 1 WHERE `type` = ? AND `name` = ? LIMIT 1", SQLEntity.Type.GROUP.ordinal(), group.getName());
			} else {
				sql.executeUpdate("DELETE FROM `permissions` WHERE `permission` = 'default' AND `world` = ? AND `type` = ?", worldName, SQLEntity.Type.GROUP.ordinal());
				sql.executeUpdate("INSERT INTO `permissions` (`name`, `permission`, `type`, `world`, `value`) VALUES (?, 'default', ?, ?, 'true')", group.getName(), SQLEntity.Type.GROUP.ordinal(), worldName);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to set default group", e);
		}
	}

	@Override
	public PermissionGroup[] getGroups() {
		String[] groupNames = SQLEntity.getEntitiesNames(sql, SQLEntity.Type.GROUP, false);
		List<PermissionGroup> groups = new LinkedList<PermissionGroup>();

		for (String groupName : groupNames) {
			groups.add(manager.getGroup(groupName));
		}

		Collections.sort(groups);

		return groups.toArray(new PermissionGroup[0]);
	}

	@Override
	public PermissionUser[] getRegisteredUsers() {
		String[] userNames = SQLEntity.getEntitiesNames(sql, SQLEntity.Type.USER, false);
		PermissionUser[] users = new PermissionUser[userNames.length];

		int index = 0;
		for (String groupName : userNames) {
			users[index++] = manager.getUser(groupName);
		}

		return users;
	}

	protected final void setupAliases(net.minecraftforge.common.Configuration config) {
		/*
		 * ConfigurationSection aliases =
		 * config.getConfigurationSection("permissions.backends.sql.aliases");
		 * 
		 * if (aliases == null) { return; }
		 * 
		 * for (Map.Entry<String, Object> entry :
		 * aliases.getValues(false).entrySet()) { sql.setAlias(entry.getKey(),
		 * entry.getValue().toString()); }
		 */
	}

	protected final void deployTables(String driver) {
		if (sql.isTableExist("permissions")) {
			return;
		}

		try {
			InputStream databaseDumpStream = getClass().getResourceAsStream("/sql/" + driver + ".sql");

			if (databaseDumpStream == null) {
				throw new Exception("Can't find appropriate database dump for used database (" + driver + "). Is it bundled?");
			}

			String deploySQL = StringUtils.readStream(databaseDumpStream);

			Logger.getLogger("Minecraft").info("Deploying default database scheme");

			for (String sqlQuery : deploySQL.trim().split(";")) {
				sqlQuery = sqlQuery.trim();
				if (sqlQuery.isEmpty()) {
					continue;
				}

				sqlQuery = sqlQuery + ";";

				sql.executeUpdate(sqlQuery);
			}

			Logger.getLogger("Minecraft").info("Database scheme deploying complete.");

		} catch (Exception e) {
			Logger.getLogger("Minecraft").severe("SQL Error: " + e.getMessage());
			Logger.getLogger("Minecraft").severe("Deploying of default scheme failed. Please initialize database manually using " + driver + ".sql");
		}
	}

	@Override
	public void dumpData(OutputStreamWriter writer) throws IOException {

		// Users
		for (PermissionUser user : manager.getUsers()) {
			// Basic info (Prefix/Suffix)
			String prefix = user.getOwnPrefix();
			String suffix = user.getOwnSuffix();
			writer.append("INSERT INTO `permissions_entity` ( `name`, `type`, `prefix`, `suffix` ) VALUES ( '" + user.getName() + "', 1, '" + (prefix == null ? "" : prefix) + "','" + (suffix == null ? "" : suffix) + "' );\n");

			// Inheritance
			for (String group : user.getGroupsNames()) {
				writer.append("INSERT INTO `permissions_inheritance` ( `child`, `parent`, `type` ) VALUES ( '" + user.getName() + "', '" + group + "',  1);\n");
			}

			// Permissions
			for (Map.Entry<String, String[]> entry : user.getAllPermissions().entrySet()) {
				for (String permission : entry.getValue()) {
					String world = entry.getKey();

					if (world == null) {
						world = "";
					}

					writer.append("INSERT INTO `permissions` ( `name`, `type`, `permission`, `world`, `value` ) VALUES ('" + user.getName() + "', 1, '" + permission + "', '" + world + "', ''); \n");
				}
			}

			// Options
			for (Map.Entry<String, Map<String, String>> entry : user.getAllOptions().entrySet()) {
				for (Map.Entry<String, String> option : entry.getValue().entrySet()) {
					String value = option.getValue().replace("'", "\\'");
					String world = entry.getKey();

					if (world == null) {
						world = "";
					}

					writer.append("INSERT INTO `permissions` ( `name`, `type`, `permission`, `world`, `value` ) VALUES ('" + user.getName() + "', 1, '" + option.getKey() + "', '" + world + "', '" + value + "' );\n");
				}
			}
		}

		PermissionGroup defaultGroup = manager.getDefaultGroup();

		// Groups
		for (PermissionGroup group : manager.getGroups()) {
			// Basic info (Prefix/Suffix)
			writer.append("INSERT INTO `permissions_entity` ( `name`, `type`, `prefix`, `suffix`, `default` ) VALUES ( '" + group.getName() + "', 0, '" + group.getOwnPrefix() + "','" + group.getOwnSuffix() + "', " + (group.equals(defaultGroup) ? "1" : "0") + " );\n");

			// Inheritance
			for (String parent : group.getParentGroupsNames()) {
				writer.append("INSERT INTO `permissions_inheritance` ( `child`, `parent`, `type` ) VALUES ( '" + group.getName() + "', '" + parent + "',  0);\n");
			}

			// Permissions
			for (Map.Entry<String, String[]> entry : group.getAllPermissions().entrySet()) {
				for (String permission : entry.getValue()) {
					String world = entry.getKey();

					if (world == null) {
						world = "";
					}

					writer.append("INSERT INTO `permissions` ( `name`, `type`, `permission`, `world`, `value`) VALUES ('" + group.getName() + "', 0, '" + permission + "', '" + world + "', '');\n");
				}
			}

			// Options
			for (Map.Entry<String, Map<String, String>> entry : group.getAllOptions().entrySet()) {
				for (Map.Entry<String, String> option : entry.getValue().entrySet()) {
					String value = option.getValue().replace("'", "\\'");
					String world = entry.getKey();

					if (world == null) {
						world = "";
					}

					writer.append("INSERT INTO `permissions` ( `name`, `type`, `permission`, `world`, `value` ) VALUES ('" + group.getName() + "', 0, '" + option.getKey() + "', '" + world + "', '" + value + "' );\n");
				}
			}
		}

		// World-inheritance
		for (WorldServer world : MinecraftServer.getServer().worldServers) {
			String[] parentWorlds = manager.getWorldInheritance(String.valueOf(world.provider.dimensionId));
			if (parentWorlds.length == 0) {
				continue;
			}

			for (String parentWorld : parentWorlds) {
				writer.append("INSERT INTO `permissions_inheritance` ( `child`, `parent`, `type` ) VALUES ( '" + String.valueOf(world.provider.dimensionId) + "', '" + parentWorld + "',  2);\n");
			}
		}

		writer.flush();
	}

	@Override
	public String[] getWorldInheritance(String world) {
		if (world == null || world.isEmpty()) {
			return new String[0];
		}

		if (!worldInheritanceCache.containsKey(world)) {
			try {
				ResultSet result = sql.select("SELECT `parent` FROM `permissions_inheritance` WHERE `child` = ? AND `type` = 2;", world);
				LinkedList<String> worldParents = new LinkedList<String>();

				while (result.next()) {
					worldParents.add(result.getString("parent"));
				}

				worldInheritanceCache.put(world, worldParents.toArray(new String[0]));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		return worldInheritanceCache.get(world);
	}

	@Override
	public void setWorldInheritance(String worldName, String[] parentWorlds) {
		if (worldName == null || worldName.isEmpty()) {
			return;
		}

		try {
			sql.executeUpdate("DELETE FROM `permissions_inheritance` WHERE `child` = ? AND `type` = 2", worldName);

			List<Object[]> records = new LinkedList<Object[]>();

			for (String parentWorld : parentWorlds) {
				records.add(new Object[] { worldName, parentWorld, 2 });
			}

			sql.insert("permissions_inheritance", new String[] { "child", "parent", "type" }, records);

			worldInheritanceCache.put(worldName, parentWorlds);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void reload() {
		worldInheritanceCache.clear();
	}
}
