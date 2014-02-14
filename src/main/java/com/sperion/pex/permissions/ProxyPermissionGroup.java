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

import java.util.Map;

import com.sperion.pex.permissions.events.PermissionEntityEvent;

public abstract class ProxyPermissionGroup extends PermissionGroup {

    protected IPermissionEntity backendEntity;

    public ProxyPermissionGroup(PermissionEntity backendEntity) {
        super(backendEntity.getName(), backendEntity.manager);

        this.backendEntity = backendEntity;

        this.setName(backendEntity.getName());

        virtual = backendEntity.isVirtual();

        this.backendEntity.initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        backendEntity.initialize();
    }

    @Override
    public String[] getWorlds() {
        return backendEntity.getWorlds();
    }

    @Override
    public String getOwnPrefix(String worldName) {
        return backendEntity.getPrefix(worldName);
    }

    @Override
    public String getOwnSuffix(String worldName) {
        return backendEntity.getSuffix(worldName);
    }

    @Override
    public void setPrefix(String prefix, String worldName) {
        backendEntity.setPrefix(prefix, worldName);

        this.clearMembersCache();

        this.callEvent(PermissionEntityEvent.Action.INFO_CHANGED);
    }

    @Override
    public void setSuffix(String suffix, String worldName) {
        backendEntity.setSuffix(suffix, worldName);

        this.clearMembersCache();

        this.callEvent(PermissionEntityEvent.Action.INFO_CHANGED);
    }

    @Override
    public boolean isVirtual() {
        return backendEntity.isVirtual();
    }

    @Override
    public Map<String, Map<String, String>> getAllOptions() {
        return backendEntity.getAllOptions();
    }

    @Override
    public Map<String, String[]> getAllPermissions() {
        return backendEntity.getAllPermissions();
    }

    @Override
    public String[] getOwnPermissions(String world) {
        return backendEntity.getPermissions(world);
    }

    @Override
    public String getOwnOption(String option, String world, String defaultValue) {
        return backendEntity.getOption(option, world, defaultValue);
    }

    @Override
    public Map<String, String> getOptions(String world) {
        return backendEntity.getOptions(world);
    }

    @Override
    public void save() {
        backendEntity.save();
        this.callEvent(PermissionEntityEvent.Action.SAVED);
    }

    @Override
    protected void removeGroup() {
        backendEntity.remove();
    }

    @Override
    public void setOption(String permission, String value, String world) {
        backendEntity.setOption(permission, value, world);

        this.clearMembersCache();

        this.callEvent(PermissionEntityEvent.Action.OPTIONS_CHANGED);
    }

    @Override
    public void setPermissions(String[] permissions, String world) {
        backendEntity.setPermissions(permissions, world);

        this.clearMembersCache();

        this.callEvent(PermissionEntityEvent.Action.PERMISSIONS_CHANGED);
    }
}
