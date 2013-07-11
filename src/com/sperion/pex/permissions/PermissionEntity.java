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
package com.sperion.pex.permissions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.sperion.pex.permissions.events.PermissionEntityEvent;

public abstract class PermissionEntity implements IPermissionEntity {

    protected PermissionManager manager;
    private String name;
    protected boolean virtual = true;
    protected Map<String, List<String>> timedPermissions = new ConcurrentHashMap<String, List<String>>();
    protected Map<String, Long> timedPermissionsTime = new ConcurrentHashMap<String, Long>();
    protected boolean debugMode = false;

    public PermissionEntity(String name, PermissionManager manager) {
        this.manager = manager;
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#initialize()
     */
    @Override
    public void initialize() {
        debugMode = this.getOptionBoolean("debug", null, debugMode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getPrefix(java.lang.String)
     */
    @Override
    public abstract String getPrefix(String worldName);

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getPrefix()
     */
    @Override
    public String getPrefix() {
        return this.getPrefix(null);
    }

    /**
     * Returns entity prefix
     * 
     */
    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#setPrefix(java.lang.String,
     * java.lang.String)
     */
    @Override
    public abstract void setPrefix(String prefix, String worldName);

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getSuffix(java.lang.String)
     */
    @Override
    public abstract String getSuffix(String worldName);

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getSuffix()
     */
    @Override
    public String getSuffix() {
        return getSuffix(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#setSuffix(java.lang.String,
     * java.lang.String)
     */
    @Override
    public abstract void setSuffix(String suffix, String worldName);

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#has(java.lang.String)
     */
    @Override
    public boolean has(String permission) {
        return this.has(permission, "0");
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#has(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean has(String permission, String dimension) {
        if (permission != null && permission.isEmpty()) { // empty permission
                                                          // for public access
                                                          // :)
            return true;
        }

        String expression = getMatchingExpression(permission, dimension);

        if (this.isDebug()) {
            Logger.getLogger("Minecraft").info(
                    "User "
                            + this.getName()
                            + " checked for \""
                            + permission
                            + "\", "
                            + (expression == null ? "no permission found"
                                    : "\"" + expression + "\" found"));
        }

        return explainExpression(expression);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getPermissions(java.lang.String)
     */
    @Override
    public abstract String[] getPermissions(String world);

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getAllPermissions()
     */
    @Override
    public abstract Map<String, String[]> getAllPermissions();

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#addPermission(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void addPermission(String permission, String world) {
        throw new UnsupportedOperationException(
                "You shouldn't call this method");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#addPermission(java.lang.String)
     */
    @Override
    public void addPermission(String permission) {
        this.addPermission(permission, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#removePermission(java.lang.String
     * , java.lang.String)
     */
    @Override
    public void removePermission(String permission, String worldName) {
        throw new UnsupportedOperationException(
                "You shouldn't call this method");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#removePermission(java.lang.String
     * )
     */
    @Override
    public void removePermission(String permission) {
        for (String world : this.getAllPermissions().keySet()) {
            this.removePermission(permission, world);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#setPermissions(java.lang.String
     * [], java.lang.String)
     */
    @Override
    public abstract void setPermissions(String[] permissions, String world);

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#setPermissions(java.lang.String
     * [])
     */
    @Override
    public void setPermissions(String[] permission) {
        this.setPermissions(permission, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getOption(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public abstract String getOption(String option, String world,
            String defaultValue);

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getOption(java.lang.String)
     */
    @Override
    public String getOption(String option) {
        // @todo Replace empty string with null
        return this.getOption(option, "", "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getOption(java.lang.String,
     * java.lang.String)
     */
    @Override
    public String getOption(String option, String world) {
        // @todo Replace empty string with null
        return this.getOption(option, world, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getOptionInteger(java.lang.String
     * , java.lang.String, int)
     */
    @Override
    public int getOptionInteger(String optionName, String world,
            int defaultValue) {
        try {
            return Integer.parseInt(this.getOption(optionName, world, Integer
                    .toString(defaultValue)));
        } catch (NumberFormatException e) {}

        return defaultValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getOptionDouble(java.lang.String
     * , java.lang.String, double)
     */
    @Override
    public double getOptionDouble(String optionName, String world,
            double defaultValue) {
        String option = this.getOption(optionName, world, Double
                .toString(defaultValue));

        try {
            return Double.parseDouble(option);
        } catch (NumberFormatException e) {}

        return defaultValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getOptionBoolean(java.lang.String
     * , java.lang.String, boolean)
     */
    @Override
    public boolean getOptionBoolean(String optionName, String world,
            boolean defaultValue) {
        String option = this.getOption(optionName, world, Boolean
                .toString(defaultValue));

        if ("false".equalsIgnoreCase(option)) {
            return false;
        } else if ("true".equalsIgnoreCase(option)) {
            return true;
        }

        return defaultValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#setOption(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public abstract void setOption(String option, String value, String world);

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#setOption(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setOption(String permission, String value) {
        this.setOption(permission, value, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getOptions(java.lang.String)
     */
    @Override
    public abstract Map<String, String> getOptions(String world);

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getAllOptions()
     */
    @Override
    public abstract Map<String, Map<String, String>> getAllOptions();

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#save()
     */
    @Override
    public abstract void save();

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#remove()
     */
    @Override
    public abstract void remove();

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#isVirtual()
     */
    @Override
    public boolean isVirtual() {
        return virtual;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#getWorlds()
     */
    @Override
    public abstract String[] getWorlds();

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getTimedPermissions(java.lang
     * .String)
     */
    @Override
    public String[] getTimedPermissions(String world) {
        if (world == null) {
            world = "";
        }

        if (!timedPermissions.containsKey(world)) {
            return new String[0];
        }

        return timedPermissions.get(world).toArray(new String[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getTimedPermissionLifetime(java
     * .lang.String, java.lang.String)
     */
    @Override
    public int getTimedPermissionLifetime(String permission, String world) {
        if (world == null) {
            world = "";
        }

        if (!timedPermissionsTime.containsKey(world + ":" + permission)) {
            return 0;
        }

        return (int) (timedPermissionsTime.get(world + ":" + permission)
                .longValue() - System.currentTimeMillis() / 1000L);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#addTimedPermission(java.lang
     * .String, java.lang.String, int)
     */
    @Override
    public void addTimedPermission(final String permission, String world,
            int lifeTime) {
        if (world == null) {
            world = "";
        }

        if (!timedPermissions.containsKey(world)) {
            timedPermissions.put(world, new LinkedList<String>());
        }

        timedPermissions.get(world).add(permission);

        final String finalWorld = world;

        if (lifeTime > 0) {
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    removeTimedPermission(permission, finalWorld);
                }
            };

            manager.registerTask(task, lifeTime);

            timedPermissionsTime.put(world + ":" + permission, System
                    .currentTimeMillis()
                    / 1000L + lifeTime);
        }

        this.callEvent(PermissionEntityEvent.Action.PERMISSIONS_CHANGED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#removeTimedPermission(java.lang
     * .String, java.lang.String)
     */
    @Override
    public void removeTimedPermission(String permission, String world) {
        if (world == null) {
            world = "";
        }

        if (!timedPermissions.containsKey(world)) {
            return;
        }

        timedPermissions.get(world).remove(permission);
        timedPermissions.remove(world + ":" + permission);

        this.callEvent(PermissionEntityEvent.Action.PERMISSIONS_CHANGED);
    }

    protected void callEvent(PermissionEntityEvent event) {
        manager.callEvent(event);
    }

    protected void callEvent(PermissionEntityEvent.Action action) {
        this.callEvent(new PermissionEntityEvent(this, action));
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final PermissionEntity other = (PermissionEntity) obj;
        return name.equals(other.name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (name != null ? name.hashCode() : 0);
        return hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.getName() + ")";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getMatchingExpression(java.lang
     * .String, java.lang.String)
     */
    @Override
    public String getMatchingExpression(String permission, String world) {
        return this.getMatchingExpression(this.getPermissions(world),
                permission);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#getMatchingExpression(java.lang
     * .String[], java.lang.String)
     */
    @Override
    public String getMatchingExpression(String[] permissions, String permission) {
        for (String expression : permissions) {
            if (isMatches(expression, permission, true)) {
                return expression;
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#isMatches(java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public boolean isMatches(String expression, String permission,
            boolean additionalChecks) {
        return manager.getPermissionMatcher().isMatches(expression, permission);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ru.tehkode.permissions.IPermissionEntity#explainExpression(java.lang.
     * String)
     */
    @Override
    public boolean explainExpression(String expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }

        return !expression.startsWith("-"); // If expression have - (minus)
                                            // before then that mean expression
                                            // are negative
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#isDebug()
     */
    @Override
    public boolean isDebug() {
        return debugMode || manager.isDebug();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ru.tehkode.permissions.IPermissionEntity#setDebug(boolean)
     */
    @Override
    public void setDebug(boolean debug) {
        debugMode = debug;
    }
}
