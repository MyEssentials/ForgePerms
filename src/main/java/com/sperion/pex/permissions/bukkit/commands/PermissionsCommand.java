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
package com.sperion.pex.permissions.bukkit.commands;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import org.bukkit.ChatColor;

import com.sperion.pex.permissions.IPermissionEntity;
import com.sperion.pex.permissions.PermissionGroup;
import com.sperion.pex.permissions.PermissionUser;
import com.sperion.pex.permissions.bukkit.PermissionsEx;
import com.sperion.pex.permissions.commands.CommandListener;
import com.sperion.pex.permissions.commands.CommandsManager;
import com.sperion.pex.permissions.commands.exceptions.AutoCompleteChoicesException;
import com.sperion.pex.utils.StringUtils;

public abstract class PermissionsCommand implements CommandListener {

    protected static final Logger logger = Logger.getLogger("Minecraft");
    protected CommandsManager manager;

    @Override
    public void onRegistered(CommandsManager manager) {
        this.manager = manager;
    }

    protected void informGroup(Object plugin, PermissionGroup group,
            String message) {
        for (PermissionUser user : group.getUsers()) {
            this.informPlayer(plugin, user.getName(), message);
        }
    }

    protected void informPlayer(Object plugin, String playerName, String message) {
        if (!(plugin instanceof PermissionsEx)
                || !((PermissionsEx) plugin).getConfig().get("permissions",
                        "informplayers_changes", false).getBoolean(false)) {
            return; // User informing is disabled
        }

        EntityPlayer player = MinecraftServer.getServer()
                .getConfigurationManager().getPlayerForUsername(playerName);
        if (player == null) {
            return;
        }
        PermissionsEx.sendChatToPlayer(player, ChatColor.BLUE + "[PermissionsEx] " + ChatColor.WHITE + message); 
        //player.sendChatToPlayer(ChatColor.BLUE + "[PermissionsEx] " + ChatColor.WHITE + message);
    }

    protected String autoCompletePlayerName(String playerName) {
        return autoCompletePlayerName(playerName, "user");
    }

    protected void printEntityInheritance(ICommandSender sender,
            PermissionGroup[] groups) {
        for (PermissionGroup group : groups) {
            String rank = "not ranked";
            if (group.isRanked()) {
                rank = "rank " + group.getRank() + " @ "
                        + group.getRankLadder();
            }
            PermissionsEx.sendChatToPlayer(sender, "   " + group.getName() + " (" + rank + ")");
            //sender.sendChatToPlayer("   " + group.getName() + " (" + rank + ")");
        }
    }

    protected String autoCompletePlayerName(String playerName, String argName) {
        if (playerName == null) {
            return null;
        }

        List<String> players = new LinkedList<String>();

        // Collect online Player names
        for (String player : MinecraftServer.getServer()
                .getConfigurationManager().getAllUsernames()) {
            if (player.equalsIgnoreCase(playerName)) {
                return player;
            }

            if (player.toLowerCase().startsWith(playerName.toLowerCase())
                    && !players.contains(player)) {
                players.add(player);
            }
        }

        // Collect registered PEX user names
        for (PermissionUser user : PermissionsEx.getPermissionManager()
                .getUsers()) {
            if (user.getName().equalsIgnoreCase(playerName)) {
                return user.getName();
            }

            if (user.getName().toLowerCase().startsWith(
                    playerName.toLowerCase())
                    && !players.contains(user.getName())) {
                players.add(user.getName());
            }
        }

        if (players.size() > 1) {
            throw new AutoCompleteChoicesException(players
                    .toArray(new String[0]), argName);
        } else if (players.size() == 1) {
            return players.get(0);
        }

        // Nothing found
        return playerName;
    }

    protected String getSenderName(ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            return ((EntityPlayer) sender).username;
        }

        return "console";
    }

    protected String autoCompleteGroupName(String groupName) {
        return this.autoCompleteGroupName(groupName, "group");
    }

    protected String autoCompleteGroupName(String groupName, String argName) {
        List<String> groups = new LinkedList<String>();

        for (PermissionGroup group : PermissionsEx.getPermissionManager()
                .getGroups()) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group.getName();
            }

            if (group.getName().toLowerCase().startsWith(
                    groupName.toLowerCase())
                    && !groups.contains(group.getName())) {
                groups.add(group.getName());
            }
        }

        if (groups.size() > 1) { // Found several choices
            throw new AutoCompleteChoicesException(groups
                    .toArray(new String[0]), argName);
        } else if (groups.size() == 1) { // Found one name
            return groups.get(0);
        }

        // Nothing found
        return groupName;
    }

    protected String autoCompleteWorldName(String worldName) {
        return this.autoCompleteWorldName(worldName, "world");
    }

    protected String autoCompleteWorldName(String worldName, String argName) {
        if (worldName == null || worldName.isEmpty()) {
            return null;
        }

        List<String> worlds = new LinkedList<String>();

        for (WorldServer world : MinecraftServer.getServer().worldServers) {
            String name = String.valueOf(world.provider.dimensionId);
            if (name.equalsIgnoreCase(worldName)) {
                return name;
            }

            if (name.toLowerCase().startsWith(worldName.toLowerCase())
                    && !worlds.contains(name)) {
                worlds.add(name);
            }
        }

        if (worlds.size() > 1) { // Found several choices
            throw new AutoCompleteChoicesException(worlds
                    .toArray(new String[0]), argName);
        } else if (worlds.size() == 1) { // Found one name
            return worlds.get(0);
        }

        return worldName;
    }

    protected String getSafeWorldName(String worldName, String userName) {
        if (worldName == null) {
            EntityPlayer player = MinecraftServer.getServer()
                    .getConfigurationManager().getPlayerForUsername(userName);

            if (player != null) {
                worldName = String
                        .valueOf(player.worldObj.provider.dimensionId);
            } else {
                worldName = "0";
            }
        }

        return worldName;
    }

    protected String autoCompletePermission(IPermissionEntity entity,
            String permission, String worldName) {
        return this.autoCompletePermission(entity, permission, worldName,
                "permission");
    }

    protected String autoCompletePermission(IPermissionEntity entity,
            String permission, String worldName, String argName) {
        if (permission == null) {
            return permission;
        }

        Set<String> permissions = new HashSet<String>();
        for (String currentPermission : entity.getPermissions(worldName)) {
            if (currentPermission.equalsIgnoreCase(permission)) {
                return currentPermission;
            }

            if (currentPermission.toLowerCase().startsWith(
                    permission.toLowerCase())) {
                permissions.add(currentPermission);
            }
        }

        if (permissions.size() > 0) {
            String[] permissionArray = permissions.toArray(new String[0]);

            if (permissionArray.length == 1) {
                return permissionArray[0];
            }

            throw new AutoCompleteChoicesException(permissionArray, argName);
        }

        return permission;
    }

    protected int getPosition(String permission, String[] permissions) {
        try {
            // permission is permission index
            int position = Integer.parseInt(permission) - 1;

            if (position < 0 || position >= permissions.length) {
                throw new RuntimeException("Wrong permission index specified!");
            }

            return position;
        } catch (NumberFormatException e) {
            // permission is permission text
            for (int i = 0; i < permissions.length; i++) {
                if (permission.equalsIgnoreCase(permissions[i])) {
                    return i;
                }
            }
        }

        throw new RuntimeException("Specified permission not found");
    }

    protected String printHierarchy(PermissionGroup parent, String worldName,
            int level) {
        StringBuilder buffer = new StringBuilder();

        PermissionGroup[] groups;
        if (parent == null) {
            groups = PermissionsEx.getPermissionManager().getGroups();
        } else {
            groups = parent.getChildGroups(worldName);
        }

        for (PermissionGroup group : groups) {
            if (parent == null && group.getParentGroups(worldName).length > 0) {
                continue;
            }

            buffer.append(StringUtils.repeat("  ", level)).append(" - ")
                    .append(group.getName()).append("\n");

            // Groups
            buffer.append(printHierarchy(group, worldName, level + 1));

            for (PermissionUser user : group.getUsers(worldName)) {
                buffer.append(StringUtils.repeat("  ", level + 1))
                        .append(" + ").append(user.getName()).append("\n");
            }
        }

        return buffer.toString();
    }

    protected String mapPermissions(String worldName, IPermissionEntity entity,
            int level) {
        StringBuilder builder = new StringBuilder();

        int index = 1;
        for (String permission : this.getPermissionsTree(entity, worldName, 0)) {
            if (level > 0) {
                builder.append("   ");
            } else {
                builder.append(index++).append(") ");
            }

            builder.append(permission);
            if (level > 0) {
                builder.append(" (from ").append(entity.getName()).append(")");
            } else {
                builder.append(" (own)");
            }
            builder.append("\n");
        }

        PermissionGroup[] parents;

        if (entity instanceof PermissionUser) {
            parents = ((PermissionUser) entity).getGroups(worldName);
        } else if (entity instanceof PermissionGroup) {
            parents = ((PermissionGroup) entity).getParentGroups(worldName);
        } else {
            throw new RuntimeException(
                    "Unknown class in hierarchy. Nag t3hk0d3 please.");
        }

        level++; // Just increment level once
        for (PermissionGroup group : parents) {
            builder.append(mapPermissions(worldName, group, level));
        }

        return builder.toString();
    }

    protected List<String> getPermissionsTree(IPermissionEntity entity,
            String world, int level) {
        List<String> permissions = new LinkedList<String>();
        Map<String, String[]> allPermissions = entity.getAllPermissions();

        String[] worldsPermissions = allPermissions.get(world);
        if (worldsPermissions != null) {
            permissions.addAll(sprintPermissions(world, worldsPermissions));
        }

        for (String parentWorld : PermissionsEx.getPermissionManager()
                .getWorldInheritance(world)) {
            if (parentWorld != null && !parentWorld.isEmpty()) {
                permissions.addAll(getPermissionsTree(entity, parentWorld,
                        level + 1));
            }
        }

        if (level == 0 && allPermissions.get("") != null) { // default world
                                                            // permissions
            permissions.addAll(sprintPermissions("common", allPermissions
                    .get("")));
        }

        return permissions;
    }

    protected List<String> sprintPermissions(String world, String[] permissions) {
        List<String> permissionList = new LinkedList<String>();

        if (permissions == null) {
            return permissionList;
        }

        for (String permission : permissions) {
            permissionList
                    .add(permission + (world != null ? " @" + world : ""));
        }

        return permissionList;
    }

    protected Object parseValue(String value) {
        if (value == null) {
            return null;
        }

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {}

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {}

        return value;
    }

    protected void sendMessage(ICommandSender sender, String message) {
        for (String messagePart : message.split("\n")) {
            PermissionsEx.sendChatToPlayer(sender, messagePart);
            //sender.sendChatToPlayer(messagePart);
        }
    }
}