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
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import pex.permissions.PermissionManager;
import pex.permissions.ProxyPermissionGroup;
import pex.permissions.backends.FileBackend;
import pex.permissions.events.PermissionEntityEvent;

/**
 * 
 * @author code
 */
public class FileGroup extends ProxyPermissionGroup {

    protected ConfigurationSection node;

    public FileGroup(String name, PermissionManager manager, FileBackend backend) {
        super(new FileEntity(name, manager, backend, "groups"));

        node = ((FileEntity) backendEntity).getConfigNode();
    }

    @Override
    public String[] getParentGroupsNamesImpl(String worldName) {
        List<String> parents = node.getStringList(FileEntity.formatPath(
                worldName, "inheritance"));

        if (parents.isEmpty()) {
            return new String[0];
        }

        return parents.toArray(new String[parents.size()]);
    }

    @Override
    public void setParentGroups(String[] parentGroups, String worldName) {
        if (parentGroups == null) {
            return;
        }

        node.set(FileEntity.formatPath(worldName, "inheritance"), Arrays
                .asList(parentGroups));

        this.save();

        this.callEvent(PermissionEntityEvent.Action.INHERITANCE_CHANGED);
    }
}
