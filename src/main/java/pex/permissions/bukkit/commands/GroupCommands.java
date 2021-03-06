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
package pex.permissions.bukkit.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import pex.permissions.PermissionGroup;
import pex.permissions.PermissionUser;
import pex.permissions.bukkit.PermissionsEx;
import pex.permissions.commands.Command;
import pex.utils.DateUtils;
import pex.utils.StringUtils;

public class GroupCommands extends PermissionsCommand {

	@Command(name = "pex", syntax = "groups list [world]", permission = "permissions.manage.groups.list", description = "List all registered groups")
	public void groupsList(Object plugin, ICommandSender sender, Map<String, String> args) {
		PermissionGroup[] groups = PermissionsEx.getPermissionManager().getGroups();
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Registered groups: ");
		for (PermissionGroup group : groups) {
			String rank = "";
			if (group.isRanked()) {
				rank = " (rank: " + group.getRank() + "@" + group.getRankLadder() + ") ";
			}

			PermissionsEx.sendChatToPlayer(sender, String.format("  %s %s %s %s[%s]", group.getName(), " #" + group.getWeight(), rank, EnumChatFormatting.DARK_GREEN, StringUtils.implode(group.getParentGroupsNames(worldName), ", ")));
		}
	}

	@Command(name = "pex", syntax = "groups", permission = "permissions.manage.groups.list", description = "List all registered groups (alias)")
	public void groupsListAlias(Object plugin, ICommandSender sender, Map<String, String> args) {
		groupsList(plugin, sender, args);
	}

	@Command(name = "pex", syntax = "group", permission = "permissions.manage.groups.list", description = "List all registered groups (alias)")
	public void groupsListAnotherAlias(Object plugin, ICommandSender sender, Map<String, String> args) {
		groupsList(plugin, sender, args);
	}

	@Command(name = "pex", syntax = "group <group> weight [weight]", permission = "permissions.manage.groups.weight.<group>", description = "Print or set group weight")
	public void groupPrintSetWeight(Object plugin, ICommandSender sender, Map<String, String> args) {
		// String groupName = this.autoCompleteGroupName(args.get("group"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(args.get("group"));

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		if (args.containsKey("weight")) {
			try {
				group.setWeight(Integer.parseInt(args.get("weight")));
			} catch (NumberFormatException e) {
				PermissionsEx.sendChatToPlayer(sender, "Error! Weight should be integer value.");
				return;
			}
		}

		PermissionsEx.sendChatToPlayer(sender, "Group " + group.getName() + " have " + group.getWeight() + " calories.");
	}

	@Command(name = "pex", syntax = "group <group> toggle debug", permission = "permissions.manage.groups.debug.<group>", description = "Toggle debug mode for group")
	public void groupToggleDebug(Object plugin, ICommandSender sender, Map<String, String> args) {
		// String groupName = this.autoCompleteGroupName(args.get("group"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(args.get("group"));

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		group.setDebug(!group.isDebug());

		PermissionsEx.sendChatToPlayer(sender, "Debug mode for group " + group.getName() + " have been " + (group.isDebug() ? "enabled" : "disabled") + "!");
	}

	@Command(name = "pex", syntax = "group <group> prefix [newprefix] [world]", permission = "permissions.manage.groups.prefix.<group>", description = "Get or set <group> prefix.")
	public void groupPrefix(Object plugin, ICommandSender sender, Map<String, String> args) {
		// String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(args.get("group"));

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		if (args.containsKey("newprefix")) {
			group.setPrefix(args.get("newprefix"), worldName);
		}

		PermissionsEx.sendChatToPlayer(sender, group.getName() + "'s prefix = \"" + group.getPrefix(worldName) + "\"");
	}

	@Command(name = "pex", syntax = "group <group> suffix [newsuffix] [world]", permission = "permissions.manage.groups.suffix.<group>", description = "Get or set <group> suffix")
	public void groupSuffix(Object plugin, ICommandSender sender, Map<String, String> args) {
		// String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(args.get("group"));

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		if (args.containsKey("newsuffix")) {
			group.setSuffix(args.get("newsuffix"), worldName);
		}

		PermissionsEx.sendChatToPlayer(sender, group.getName() + "'s suffix is = \"" + group.getSuffix(worldName) + "\"");
	}

	@Command(name = "pex", syntax = "group <group> create [parents]", permission = "permissions.manage.groups.create.<group>", description = "Create <group> and/or set [parents]")
	public void groupCreate(Object plugin, ICommandSender sender, Map<String, String> args) {
		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(args.get("group"));

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		if (!group.isVirtual()) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group " + args.get("group") + " already exists");
			return;
		}

		if (args.get("parents") != null) {
			String[] parents = args.get("parents").split(",");
			List<PermissionGroup> groups = new LinkedList<PermissionGroup>();

			for (String parent : parents) {
				groups.add(PermissionsEx.getPermissionManager().getGroup(parent));
			}

			group.setParentGroups(groups.toArray(new PermissionGroup[0]), null);
		}

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Group " + group.getName() + " created!");

		group.save();
	}

	@Command(name = "pex", syntax = "group <group> delete", permission = "permissions.manage.groups.remove.<group>", description = "Remove <group>")
	public void groupDelete(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Group " + group.getName() + " removed!");

		group.remove();
		PermissionsEx.getPermissionManager().resetGroup(group.getName());
		group = null;
	}

	/**
	 * Group inheritance
	 */
	@Command(name = "pex", syntax = "group <group> parents [world]", permission = "permissions.manage.groups.inheritance.<group>", description = "List parents for <group> (alias)")
	public void groupListParentsAlias(Object plugin, ICommandSender sender, Map<String, String> args) {
		groupListParents(plugin, sender, args);
	}

	@Command(name = "pex", syntax = "group <group> parents list [world]", permission = "permissions.manage.groups.inheritance.<group>", description = "List parents for <group>")
	public void groupListParents(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		if (group.getParentGroups(worldName).length == 0) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group " + group.getName() + " doesn't have parents");
			return;
		}

		PermissionsEx.sendChatToPlayer(sender, "Group " + group.getName() + " parents:");

		for (PermissionGroup parent : group.getParentGroups(worldName)) {
			PermissionsEx.sendChatToPlayer(sender, "  " + parent.getName());
		}

	}

	@Command(name = "pex", syntax = "group <group> parents set <parents> [world]", permission = "permissions.manage.groups.inheritance.<group>", description = "Set parent(s) for <group> (single or comma-separated list)")
	public void groupSetParents(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		if (args.get("parents") != null) {
			String[] parents = args.get("parents").split(",");
			List<PermissionGroup> groups = new LinkedList<PermissionGroup>();

			for (String parent : parents) {
				PermissionGroup parentGroup = PermissionsEx.getPermissionManager().getGroup(this.autoCompleteGroupName(parent));

				if (parentGroup != null && !groups.contains(parentGroup)) {
					groups.add(parentGroup);
				}
			}

			group.setParentGroups(groups.toArray(new PermissionGroup[0]), worldName);

			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Group " + group.getName() + " inheritance updated!");

			group.save();
		}
	}

	@Command(name = "pex", syntax = "group <group> parents add <parents> [world]", permission = "permissions.manage.groups.inheritance.<group>", description = "Set parent(s) for <group> (single or comma-separated list)")
	public void groupAddParents(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		if (args.get("parents") != null) {
			String[] parents = args.get("parents").split(",");
			List<PermissionGroup> groups = new LinkedList<PermissionGroup>(Arrays.asList(group.getParentGroups(worldName)));

			for (String parent : parents) {
				PermissionGroup parentGroup = PermissionsEx.getPermissionManager().getGroup(this.autoCompleteGroupName(parent));

				if (parentGroup != null && !groups.contains(parentGroup)) {
					groups.add(parentGroup);
				}
			}

			group.setParentGroups(groups.toArray(new PermissionGroup[0]), worldName);

			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Group " + group.getName() + " inheritance updated!");

			group.save();
		}
	}

	@Command(name = "pex", syntax = "group <group> parents remove <parents> [world]", permission = "permissions.manage.groups.inheritance.<group>", description = "Set parent(s) for <group> (single or comma-separated list)")
	public void groupRemoveParents(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		if (args.get("parents") != null) {
			String[] parents = args.get("parents").split(",");
			List<PermissionGroup> groups = new LinkedList<PermissionGroup>(Arrays.asList(group.getParentGroups(worldName)));

			for (String parent : parents) {
				PermissionGroup parentGroup = PermissionsEx.getPermissionManager().getGroup(this.autoCompleteGroupName(parent));

				groups.remove(parentGroup);
			}

			group.setParentGroups(groups.toArray(new PermissionGroup[0]), worldName);

			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Group " + group.getName() + " inheritance updated!");

			group.save();
		}
	}

	/**
	 * Group permissions
	 */
	@Command(name = "pex", syntax = "group <group>", permission = "permissions.manage.groups.permissions.<group>", description = "List all <group> permissions (alias)")
	public void groupListAliasPermissions(Object plugin, ICommandSender sender, Map<String, String> args) {
		groupListPermissions(plugin, sender, args);
	}

	@Command(name = "pex", syntax = "group <group> list [world]", permission = "permissions.manage.groups.permissions.<group>", description = "List all <group> permissions in [world]")
	public void groupListPermissions(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		PermissionsEx.sendChatToPlayer(sender, groupName + " are member of:");
		printEntityInheritance(sender, group.getParentGroups());

		for (String world : group.getAllParentGroups().keySet()) {
			if (world == null) {
				continue;
			}

			PermissionsEx.sendChatToPlayer(sender, "  @" + world + ":");
			printEntityInheritance(sender, group.getAllParentGroups().get(world));
		}

		PermissionsEx.sendChatToPlayer(sender, "Group " + group.getName() + "'s permissions:");
		sendMessage(sender, mapPermissions(worldName, group, 0));

		PermissionsEx.sendChatToPlayer(sender, "Group " + group.getName() + "'s Options: ");
		for (Map.Entry<String, String> option : group.getOptions(worldName).entrySet()) {
			PermissionsEx.sendChatToPlayer(sender, "  " + option.getKey() + " = \"" + option.getValue() + "\"");
		}
	}

	@Command(name = "pex", syntax = "group <group> add <permission> [world]", permission = "permissions.manage.groups.permissions.<group>", description = "Add <permission> to <group> in [world]")
	public void groupAddPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		group.addPermission(args.get("permission"), worldName);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Permission \"" + args.get("permission") + "\" added to " + group.getName() + " !");

		informGroup(plugin, group, "Your permissions have been changed");
	}

	@Command(name = "pex", syntax = "group <group> set <option> <value> [world]", permission = "permissions.manage.groups.permissions.<group>", description = "Set <option> <value> for <group> in [world]")
	public void groupSetOption(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		group.setOption(args.get("option"), args.get("value"), worldName);

		if (args.containsKey("value") && args.get("value").isEmpty()) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Option \"" + args.get("option") + "\" cleared!");
		} else {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Option \"" + args.get("option") + "\" set!");
		}

		informGroup(plugin, group, "Your permissions has been changed");
	}

	@Command(name = "pex", syntax = "group <group> remove <permission> [world]", permission = "permissions.manage.groups.permissions.<group>", description = "Remove <permission> from <group> in [world]")
	public void groupRemovePermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		String permission = this.autoCompletePermission(group, args.get("permission"), worldName);

		group.removePermission(permission, worldName);
		group.removeTimedPermission(permission, worldName);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Permission \"" + permission + "\" removed from " + group.getName() + " !");

		informGroup(plugin, group, "Your permissions have been changed");
	}

	@Command(name = "pex", syntax = "group <group> swap <permission> <targetPermission> [world]", permission = "permissions.manage.groups.permissions.<group>", description = "Swap <permission> and <targetPermission> in permission list. Could be number or permission itself")
	public void userSwapPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist");
			return;
		}

		String[] permissions = group.getOwnPermissions(worldName);

		try {
			int sourceIndex = getPosition(this.autoCompletePermission(group, args.get("permission"), worldName, "permission"), permissions);
			int targetIndex = getPosition(this.autoCompletePermission(group, args.get("targetPermission"), worldName, "targetPermission"), permissions);

			String targetPermission = permissions[targetIndex];

			permissions[targetIndex] = permissions[sourceIndex];
			permissions[sourceIndex] = targetPermission;

			group.setPermissions(permissions, worldName);

			PermissionsEx.sendChatToPlayer(sender, "Permissions swapped!");
		} catch (Throwable e) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Error: " + e.getMessage());
		}
	}

	@Command(name = "pex", syntax = "group <group> timed add <permission> [lifetime] [world]", permission = "permissions.manage.groups.permissions.timed.<group>", description = "Add timed <permission> to <group> with [lifetime] in [world]")
	public void groupAddTimedPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		int lifetime = 0;

		if (args.containsKey("lifetime")) {
			lifetime = DateUtils.parseInterval(args.get("lifetime"));
		}

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group does not exist");
			return;
		}

		group.addTimedPermission(args.get("permission"), worldName, lifetime);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Timed permission added!");
		informGroup(plugin, group, "Your permissions have been changed!");

		logger.info("Group " + groupName + " get timed permission \"" + args.get("permission") + "\" " + (lifetime > 0 ? "for " + lifetime + " seconds " : " ") + "from " + getSenderName(sender));
	}

	@Command(name = "pex", syntax = "group <group> timed remove <permission> [world]", permission = "permissions.manage.groups.permissions.timed.<group>", description = "Remove timed <permissions> for <group> in [world]")
	public void groupRemoveTimedPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group does not exist");
			return;
		}

		group.removeTimedPermission(args.get("permission"), worldName);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Timed permission \"" + args.get("permission") + "\" removed!");
		informGroup(plugin, group, "Your permissions have been changed!");
	}

	/**
	 * Group users management
	 */
	@Command(name = "pex", syntax = "group <group> users", permission = "permissions.manage.membership.<group>", description = "List all users in <group>")
	public void groupUsersList(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));

		PermissionUser[] users = PermissionsEx.getPermissionManager().getUsers(groupName);

		if (users == null || users.length == 0) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group doesn't exist or empty");
		}

		PermissionsEx.sendChatToPlayer(sender, "Group " + groupName + " users:");

		for (PermissionUser user : users) {
			PermissionsEx.sendChatToPlayer(sender, "   " + user.getName());
		}
	}

	@Command(name = "pex", syntax = "group <group> user add <user> [world]", permission = "permissions.manage.membership.<group>", description = "Add <user> (single or comma-separated list) to <group>")
	public void groupUsersAdd(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		String users[];

		if (!args.get("user").contains(",")) {
			users = new String[] { args.get("user") };
		} else {
			users = args.get("user").split(",");
		}

		for (String userName : users) {
			userName = this.autoCompletePlayerName(userName);
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

			if (user == null) {
				PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
				return;
			}

			user.addGroup(groupName, worldName);

			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "User " + user.getName() + " added to " + groupName + " !");
			informPlayer(plugin, userName, "You are assigned to \"" + groupName + "\" group");
		}
	}

	@Command(name = "pex", syntax = "group <group> user remove <user> [world]", permission = "permissions.manage.membership.<group>", description = "Add <user> (single or comma-separated list) to <group>")
	public void groupUsersRemove(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		String users[];

		if (!args.get("user").contains(",")) {
			users = new String[] { args.get("user") };
		} else {
			users = args.get("user").split(",");
		}

		for (String userName : users) {
			userName = this.autoCompletePlayerName(userName);
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

			if (user == null) {
				PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
				return;
			}

			user.removeGroup(groupName, worldName);

			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "User " + user.getName() + " removed from " + args.get("group") + " !");
			informPlayer(plugin, userName, "You were removed from \"" + groupName + "\" group");

		}
	}

	@Command(name = "pex", syntax = "default group [world]", permission = "permissions.manage.groups.inheritance", description = "Print default group for specified world")
	public void groupDefaultCheck(Object plugin, ICommandSender sender, Map<String, String> args) {
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup defaultGroup = PermissionsEx.getPermissionManager().getDefaultGroup(worldName);
		PermissionsEx.sendChatToPlayer(sender, "Default group in " + worldName + " world is " + defaultGroup.getName() + " group");
	}

	@Command(name = "pex", syntax = "set default group <group> [world]", permission = "permissions.manage.groups.inheritance", description = "Set default group for specified world")
	public void groupDefaultSet(Object plugin, ICommandSender sender, Map<String, String> args) {
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);

		if (group == null || group.isVirtual()) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Specified group doesn't exist");
			return;
		}

		PermissionsEx.getPermissionManager().setDefaultGroup(group, worldName);
		PermissionsEx.sendChatToPlayer(sender, "New default group in " + worldName + " world is " + group.getName() + " group");
	}
}
