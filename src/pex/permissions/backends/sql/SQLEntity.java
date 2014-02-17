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
package pex.permissions.backends.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pex.permissions.PermissionEntity;
import pex.permissions.PermissionManager;

/**
 * 
 * @author code
 */
public class SQLEntity extends PermissionEntity {

    public enum Type {

        GROUP, USER
    }

    protected SQLConnection db;
    protected Map<String, List<String>> worldsPermissions = null;
    protected Map<String, Map<String, String>> worldsOptions = null;
    protected List<String> commonPermissions = null;
    protected Map<String, String> commonOptions = null;
    protected Map<String, Set<String>> parents = null;
    protected Type type;
    protected String prefix;
    protected String suffix;

    public SQLEntity(String name, PermissionManager manager,
            SQLEntity.Type type, SQLConnection db) {
        super(name, manager);
        this.db = db;
        this.type = type;

        this.fetchInfo();
        this.fetchPermissions();
        this.fetchInheritance();
    }

    public static String[] getEntitiesNames(SQLConnection sql, Type type,
            boolean defaultOnly) {
        try {
            List<String> entities = new LinkedList<String>();

            ResultSet result = sql.select(
                    "SELECT name FROM `permissions_entity` WHERE `type` = ? "
                            + (defaultOnly ? " AND `default` = 1" : ""), type
                            .ordinal());

            while (result.next()) {
                entities.add(result.getString("name"));
            }

            result.close();

            return entities.toArray(new String[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getWorlds() {
        Set<String> worlds = new HashSet<String>();

        worlds.addAll(worldsOptions.keySet());
        worlds.addAll(worldsPermissions.keySet());
        worlds.addAll(parents.keySet());

        return worlds.toArray(new String[0]);
    }

    @Override
    public String getPrefix(String worldName) {
        return worldName == null || worldName.isEmpty() ? prefix : this
                .getOption("prefix", worldName);
    }

    @Override
    public String getSuffix(String worldName) {
        return worldName == null || worldName.isEmpty() ? suffix : this
                .getOption("suffix", worldName);
    }

    @Override
    public void setPrefix(String prefix, String worldName) {
        if (worldName == null || worldName.isEmpty()) {
            this.prefix = prefix;
            this.updateInfo();
        } else {
            this.setOption("prefix", prefix, worldName);
        }
    }

    @Override
    public void setSuffix(String suffix, String worldName) {
        if (worldName == null || worldName.isEmpty()) {
            this.suffix = suffix;
            this.updateInfo();
        } else {
            this.setOption("suffix", suffix, worldName);
        }
    }

    public String[] getParentNames(String worldName) {
        if (parents == null) {
            this.fetchInheritance();
        }

        if (parents.containsKey(worldName)) {
            return parents.get(worldName).toArray(new String[0]);
        }

        return new String[0];
    }

    @Override
    public String[] getPermissions(String world) {
        List<String> permissions = new LinkedList<String>();

        if (commonPermissions == null) {
            this.fetchPermissions();
        }

        if (world != null && !world.isEmpty()) {
            List<String> worldPermissions = worldsPermissions.get(world);
            if (worldPermissions != null) {
                permissions.addAll(worldPermissions);
            }
        } else {
            permissions = commonPermissions;
        }

        return permissions.toArray(new String[0]);
    }

    @Override
    public String getOption(String option, String world, String defaultValue) {
        if (world != null && !world.isEmpty()
                && worldsOptions.containsKey(world)) {
            if (worldsOptions.get(world).containsKey(option)) {
                return worldsOptions.get(world).get(option);
            }
        }

        if ((world == null || world.isEmpty())
                && commonOptions.containsKey(option)) {
            return commonOptions.get(option);
        }

        return defaultValue;
    }

    @Override
    public void setOption(String option, String value, String world) {
        if (option == null || option.isEmpty()) {
            return;
        }

        if (world == null) {
            world = "";
        }

        if (value == null || value.isEmpty()) {
            try {
                db.executeUpdate(
                        "DELETE FROM `permissions` WHERE `name` = ? AND `permission` = ? AND `type` = ? AND `world` = ?",
                        this.getName(), option, type.ordinal(), world);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (!world.isEmpty() && worldsOptions.containsKey(world)) {
                worldsOptions.get(world).remove(option);
            } else {
                commonOptions.remove(option);
            }

            return;
        }

        Boolean newOption = true;
        if (commonOptions == null) {
            this.fetchPermissions();
        }

        if (!world.isEmpty() && worldsOptions.containsKey(world)
                && worldsOptions.get(world).containsKey(option)) {
            newOption = false;
        } else if (world.isEmpty() && commonOptions.containsKey(option)) {
            newOption = false;
        }

        try {
            if (newOption) {
                db.executeUpdate(
                        "INSERT INTO `permissions` (`name`, `permission`, `value`, `world`, `type`) VALUES (?, ?, ?, ?, ?)",
                        this.getName(), option, value, world, type.ordinal());
            } else {
                db.executeUpdate(
                        "UPDATE `permissions` SET `value` = ? WHERE `name` = ? AND `type` = ? AND `permission` = ?",
                        value, this.getName(), type.ordinal(), option);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (this.isVirtual()) {
            this.save();
        }

        // Refetch options
        this.fetchPermissions();
    }

    public void setParents(String[] parentGroups, String worldName) {
        try {
            // Clean out existing records
            if (worldName != null) { // damn NULL
                db.executeUpdate(
                        "DELETE FROM `permissions_inheritance` WHERE `child` = ? AND `type` = ? AND `world` = ?",
                        this.getName(), type.ordinal(), worldName);
            } else {
                db.executeUpdate(
                        "DELETE FROM `permissions_inheritance` WHERE `child` = ? AND `type` = ? AND IFNULL(`world`, 1)",
                        this.getName(), type.ordinal());
            }

            List<Object[]> rows = new LinkedList<Object[]>();
            for (String group : parentGroups) {
                if (group == null || group.isEmpty()) {
                    continue;
                }

                rows.add(new Object[] { this.getName(), group, type.ordinal(),
                        worldName });
            }

            db.insert("permissions_inheritance", new String[] { "child",
                    "parent", "type", "world" }, rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (this.isVirtual()) {
            this.save();
        }

        // reload inherirance
        parents = null;
        this.fetchInheritance();
    }

    @Override
    public Map<String, String> getOptions(String world) {
        Map<String, String> options = world == null ? commonOptions
                : worldsOptions.get(world);

        return options != null ? options : new HashMap<String, String>();
    }

    @Override
    public Map<String, String[]> getAllPermissions() {
        Map<String, String[]> allPermissions = new HashMap<String, String[]>();

        allPermissions.put(null, commonPermissions.toArray(new String[0]));

        for (Map.Entry<String, List<String>> entry : worldsPermissions
                .entrySet()) {
            allPermissions.put(entry.getKey(), entry.getValue().toArray(
                    new String[0]));
        }

        return allPermissions;
    }

    @Override
    public Map<String, Map<String, String>> getAllOptions() {
        Map<String, Map<String, String>> allOptions = new HashMap<String, Map<String, String>>();

        allOptions.put(null, commonOptions);

        for (Map.Entry<String, Map<String, String>> entry : worldsOptions
                .entrySet()) {
            allOptions.put(entry.getKey(), entry.getValue());
        }

        return allOptions;
    }

    @Override
    public void setPermissions(String[] permissions, String world) {
        if (world == null) {
            world = "";
        }

        try {
            db.executeUpdate(
                    "DELETE FROM `permissions` WHERE `name` = ? AND `type` = ? AND `world` = ? AND `value` = ''",
                    this.getName(), type.ordinal(), world);

            for (int i = permissions.length - 1; i >= 0; i--) { // insert in
                                                                // reverse order
                db.executeUpdate(
                        "INSERT INTO `permissions` (`name`, `permission`, `value`, `world`, `type`) VALUES (?, ?, '', ?, ?)",
                        this.getName(), permissions[i], world, type.ordinal());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (this.isVirtual()) {
            this.save();
        }

        this.fetchPermissions();
    }

    @Override
    public void save() {
        this.updateInfo();
    }

    @Override
    public void remove() {
        try {
            // clear inheritance info
            db.executeUpdate(
                    "DELETE FROM `permissions_inheritance` WHERE `child` = ? AND `type` = ?",
                    this.getName(), type.ordinal());
            // clear permissions
            db.executeUpdate(
                    "DELETE FROM `permissions` WHERE `name` = ? AND `type` = ?",
                    this.getName(), type.ordinal());
            // clear info
            db.executeUpdate(
                    "DELETE FROM `permissions_entity` WHERE `name` = ? AND `type` = ?",
                    this.getName(), type.ordinal());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        virtual = true;
        commonOptions.clear();
        commonPermissions.clear();
        worldsOptions.clear();
        worldsPermissions.clear();
        parents.clear();
    }

    protected void updateInfo() {
        String sql;
        if (this.isVirtual()) { // This section are suspicious, here was problem
                                // which are resolved mysticaly. Keep eye on it.
            sql = "INSERT INTO `permissions_entity` (`prefix`, `suffix`, `name`, `type`) VALUES (?, ?, ?, ?)";
        } else {
            sql = "UPDATE `permissions_entity` SET `prefix` = ?, `suffix` = ? WHERE `name` = ? AND `type` = ?";
        }

        try {
            db.executeUpdate(sql, prefix, suffix, this.getName(), type
                    .ordinal());
        } catch (SQLException e) {
            if (this.isVirtual()) {
                virtual = false;
                this.updateInfo(); // try again
            }

            throw new RuntimeException(e);
        }

        virtual = false;
    }

    protected final void fetchPermissions() {
        worldsOptions = new HashMap<String, Map<String, String>>();
        worldsPermissions = new HashMap<String, List<String>>();
        commonOptions = new HashMap<String, String>();
        commonPermissions = new LinkedList<String>();

        try {
            ResultSet results = db
                    .select("SELECT `permission`, `world`, `value` FROM `permissions` WHERE `name` = ? AND `type` = ? ORDER BY `id` DESC",
                            this.getName(), type.ordinal());
            while (results.next()) {
                String permission = results.getString("permission").trim();
                String world = results.getString("world").trim();
                String value = results.getString("value");

                // @TODO: to this in more optimal way
                if (value.isEmpty()) {
                    if (!world.isEmpty()) {
                        List<String> worldPermissions = worldsPermissions
                                .get(world);
                        if (worldPermissions == null) {
                            worldPermissions = new LinkedList<String>();
                            worldsPermissions.put(world, worldPermissions);
                        }

                        worldPermissions.add(permission);
                    } else {
                        commonPermissions.add(permission);
                    }
                } else {
                    if (!world.isEmpty()) {
                        Map<String, String> worldOptions = worldsOptions
                                .get(world);
                        if (worldOptions == null) {
                            worldOptions = new HashMap<String, String>();
                            worldsOptions.put(world, worldOptions);
                        }

                        worldOptions.put(permission, value);
                    } else {
                        commonOptions.put(permission, value);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected final void fetchInheritance() {
        try {
            parents = new HashMap<String, Set<String>>();

            ResultSet results = db
                    .select("SELECT `parent`, `world` FROM `permissions_inheritance` WHERE `child` = ? AND `type` = ? ORDER BY `id` DESC",
                            this.getName(), type.ordinal());

            while (results.next()) {
                String parentName = results.getString(1);
                String worldName = results.getString(2);

                if (!parents.containsKey(worldName)) {
                    parents.put(worldName, new HashSet<String>());
                }

                parents.get(worldName).add(parentName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected final void fetchInfo() {
        try {
            ResultSet result = db
                    .select("SELECT `name`, `prefix`, `suffix` FROM `permissions_entity` WHERE `name` = ? AND `type` = ? LIMIT 1",
                            this.getName(), type.ordinal());

            if (result.next()) {
                prefix = result.getString("prefix");
                suffix = result.getString("suffix");

                // For teh case-insensetivity
                this.setName(result.getString("name"));

                virtual = false;
            } else {
                prefix = "";
                suffix = "";
                virtual = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
