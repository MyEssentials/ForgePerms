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
package pex.permissions.backends.file;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import pex.permissions.PermissionEntity;
import pex.permissions.PermissionManager;
import pex.permissions.backends.FileBackend;

public class FileEntity extends PermissionEntity {

    protected ConfigurationSection node;
    protected FileBackend backend;
    protected String nodePath;

    public FileEntity(String entityName, PermissionManager manager,
            FileBackend backend, String baseNode) {
        super(entityName, manager);

        this.backend = backend;
        node = this.getNode(baseNode, this.getName());
    }

    protected final ConfigurationSection getNode(String baseNode,
            String entityName) {
        nodePath = FileBackend.buildPath(baseNode, entityName);

        ConfigurationSection entityNode = backend.permissions
                .getConfigurationSection(nodePath);

        if (entityNode != null) {
            virtual = false;
            return entityNode;
        }

        ConfigurationSection users = backend.permissions
                .getConfigurationSection(baseNode);

        if (users != null) {
            for (Map.Entry<String, Object> entry : users.getValues(false)
                    .entrySet()) {
                if (entry.getKey().equalsIgnoreCase(entityName)
                        && entry.getValue() instanceof ConfigurationSection) {
                    this.setName(entry.getKey());
                    nodePath = FileBackend.buildPath(baseNode, this.getName());
                    virtual = false;

                    return (ConfigurationSection) entry.getValue();
                }
            }
        }

        virtual = true;

        // Silly workaround for empty nodes
        ConfigurationSection section = backend.permissions
                .createSection(nodePath);
        backend.permissions.set(nodePath, null);

        return section;
    }

    public ConfigurationSection getConfigNode() {
        return node;
    }

    @Override
    public String[] getPermissions(String world) {
        List<String> permissions = node.getStringList(formatPath(world,
                "permissions"));

        if (permissions == null) {
            return new String[0];
        }

        return permissions.toArray(new String[permissions.size()]);
    }

    @Override
    public void setPermissions(String[] permissions, String world) {
        node.set(formatPath(world, "permissions"),
                permissions.length > 0 ? Arrays.asList(permissions) : null);

        this.save();
    }

    @Override
    public String[] getWorlds() {
        ConfigurationSection worldsSection = node
                .getConfigurationSection("worlds");

        if (worldsSection == null) {
            return new String[0];
        }

        return worldsSection.getKeys(false).toArray(new String[0]);
    }

    @Override
    public Map<String, String> getOptions(String world) {

        ConfigurationSection optionsSection = node
                .getConfigurationSection(formatPath(world, "options"));

        if (optionsSection != null) {
            return this.collectOptions(optionsSection);
        }

        return new HashMap<String, String>(0);
    }

    @Override
    public String getOption(String option, String world, String defaultValue) {
        return node.getString(formatPath(world, "options", option),
                defaultValue);
    }

    @Override
    public void setOption(String option, String value, String world) {
        node.set(formatPath(world, "options", option), value);

        this.save();
    }

    @Override
    public String getPrefix(String worldName) {
        return node.getString(formatPath(worldName, "prefix"));
    }

    @Override
    public String getSuffix(String worldName) {
        return node.getString(formatPath(worldName, "suffix"));
    }

    @Override
    public void setPrefix(String prefix, String worldName) {
        node.set(formatPath(worldName, "prefix"), prefix);

        this.save();
    }

    @Override
    public void setSuffix(String suffix, String worldName) {
        node.set(formatPath(worldName, "suffix"), suffix);

        this.save();
    }

    @Override
    public Map<String, String[]> getAllPermissions() {
        Map<String, String[]> allPermissions = new HashMap<String, String[]>();

        // Common permissions
        List<String> commonPermissions = node.getStringList("permissions");
        if (commonPermissions != null) {
            allPermissions.put(null, commonPermissions.toArray(new String[0]));
        }

        // World-specific permissions
        ConfigurationSection worldsSection = node
                .getConfigurationSection("worlds");
        if (worldsSection != null) {
            for (String world : worldsSection.getKeys(false)) {
                List<String> worldPermissions = node.getStringList(FileBackend
                        .buildPath("worlds", world, "permissions"));
                if (commonPermissions != null) {
                    allPermissions.put(world, worldPermissions
                            .toArray(new String[0]));
                }
            }
        }

        return allPermissions;
    }

    @Override
    public Map<String, Map<String, String>> getAllOptions() {
        Map<String, Map<String, String>> allOptions = new HashMap<String, Map<String, String>>();

        allOptions.put(null, this.getOptions(null));

        for (String worldName : this.getWorlds()) {
            allOptions.put(worldName, this.getOptions(worldName));
        }

        return allOptions;
    }

    private Map<String, String> collectOptions(ConfigurationSection section) {
        Map<String, String> options = new LinkedHashMap<String, String>();

        for (String key : section.getKeys(true)) {
            if (section.isConfigurationSection(key)) {
                continue;
            }

            options.put(key.replace(
                    section.getRoot().options().pathSeparator(), '.'), section
                    .getString(key));
        }

        return options;
    }

    @Override
    public void save() {
        backend.permissions.set(nodePath, node);

        backend.save();
        virtual = false;
    }

    @Override
    public void remove() {
        backend.permissions.set(nodePath, null);

        backend.save();
    }

    protected static String formatPath(String worldName, String node,
            String value) {
        String path = FileBackend.buildPath(node, value);

        if (worldName != null && !worldName.isEmpty()) {
            path = FileBackend.buildPath("worlds", worldName, path);
        }

        return path;
    }

    protected static String formatPath(String worldName, String node) {
        String path = node;

        if (worldName != null && !worldName.isEmpty()) {
            path = FileBackend.buildPath("worlds", worldName, path);
        }

        return path;
    }
}
