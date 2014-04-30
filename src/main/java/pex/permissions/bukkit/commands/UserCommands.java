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

import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import pex.permissions.PermissionGroup;
import pex.permissions.PermissionManager;
import pex.permissions.PermissionUser;
import pex.permissions.bukkit.PermissionsEx;
import pex.permissions.commands.Command;
import pex.utils.DateUtils;
import pex.utils.StringUtils;

public class UserCommands extends PermissionsCommand {

	@Command(name = "pex", syntax = "users list", permission = "permissions.manage.users", description = "List all registered users")
	public void usersList(Object plugin, ICommandSender sender, Map<String, String> args) {
		PermissionUser[] users = PermissionsEx.getPermissionManager().getUsers();

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Currently registered users: ");
		for (PermissionUser user : users) {
			PermissionsEx.sendChatToPlayer(sender, " " + user.getName() + " " + EnumChatFormatting.DARK_GREEN + "[" + StringUtils.implode(user.getGroupsNames(), ", ") + "]");
		}
	}

	@Command(name = "pex", syntax = "users", permission = "permissions.manage.users", description = "List all registered users (alias)", isPrimary = true)
	public void userListAlias(Object plugin, ICommandSender sender, Map<String, String> args) {
		usersList(plugin, sender, args);
	}

	@Command(name = "pex", syntax = "user", permission = "permissions.manage.users", description = "List all registered users (alias)")
	public void userListAnotherAlias(Object plugin, ICommandSender sender, Map<String, String> args) {
		usersList(plugin, sender, args);
	}

	/**
	 * User permission management
	 */
	@Command(name = "pex", syntax = "user <user>", permission = "permissions.manage.users.permissions.<user>", description = "List user permissions (list alias)")
	public void userListAliasPermissions(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		PermissionsEx.sendChatToPlayer(sender, userName + " are member of:");
		printEntityInheritance(sender, user.getGroups());

		for (String world : user.getAllGroups().keySet()) {
			if (world == null) {
				continue;
			}

			PermissionsEx.sendChatToPlayer(sender, "  @" + world + ":");
			printEntityInheritance(sender, user.getAllGroups().get(world));
		}

		PermissionsEx.sendChatToPlayer(sender, userName + "'s permissions:");

		sendMessage(sender, mapPermissions(worldName, user, 0));

		PermissionsEx.sendChatToPlayer(sender, userName + "'s options:");
		for (Map.Entry<String, String> option : user.getOptions(worldName).entrySet()) {
			PermissionsEx.sendChatToPlayer(sender, "  " + option.getKey() + " = \"" + option.getValue() + "\"");
		}
	}

	@Command(name = "pex", syntax = "user <user> list [world]", permission = "permissions.manage.users.permissions.<user>", description = "List user permissions")
	public void userListPermissions(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		PermissionsEx.sendChatToPlayer(sender, userName + "'s permissions:");

		for (String permission : user.getPermissions(worldName)) {
			PermissionsEx.sendChatToPlayer(sender, "  " + permission);
		}

	}

	@Command(name = "pex", syntax = "user <user> superperms", permission = "permissions.manage.users.permissions.<user>", description = "List user actual superperms")
	public void userListSuperPermissions(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));

		EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(userName);
		if (player == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Player not found (offline?)");
			return;
		}

		PermissionsEx.sendChatToPlayer(sender, userName + "'s superperms: ");
		PermissionsEx.sendChatToPlayer(sender, "disabled");
		/*
		 * for (PermissionAttachmentInfo info :
		 * player.getEffectivePermissions()) { String pluginName = "built-in";
		 * 
		 * if (info.getAttachment() != null && info.getAttachment().getPlugin()
		 * != null) { pluginName =
		 * info.getAttachment().getPlugin().getDescription().getName(); }
		 * 
		 * PermissionsEx.sendChatToPlayer(sender, " '" + EnumChatFormatting.GREEN +
		 * info.getPermission() + EnumChatFormatting.WHITE + "' = " + EnumChatFormatting.BLUE +
		 * info.getValue() + EnumChatFormatting.WHITE + " by " + EnumChatFormatting.DARK_GREEN +
		 * pluginName); }
		 */
	}

	@Command(name = "pex", syntax = "user <user> prefix [newprefix] [world]", permission = "permissions.manage.users.prefix.<user>", description = "Get or set <user> prefix")
	public void userPrefix(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		if (args.containsKey("newprefix")) {
			user.setPrefix(args.get("newprefix"), worldName);
		}

		PermissionsEx.sendChatToPlayer(sender, user.getName() + "'s prefix = \"" + user.getPrefix() + "\"");
	}

	@Command(name = "pex", syntax = "user <user> suffix [newsuffix] [world]", permission = "permissions.manage.users.suffix.<user>", description = "Get or set <user> suffix")
	public void userSuffix(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		if (args.containsKey("newsuffix")) {
			user.setSuffix(args.get("newsuffix"), worldName);
		}

		PermissionsEx.sendChatToPlayer(sender, user.getName() + "'s suffix = \"" + user.getSuffix() + "\"");
	}

	@Command(name = "pex", syntax = "user <user> toggle debug", permission = "permissions.manage.<user>", description = "Toggle debug only for <user>")
	public void userToggleDebug(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		user.setDebug(!user.isDebug());

		PermissionsEx.sendChatToPlayer(sender, "Debug mode for user " + userName + " " + (user.isDebug() ? "enabled" : "disabled") + "!");
	}

	@Command(name = "pex", syntax = "user <user> check <permission> [world]", permission = "permissions.manage.<user>", description = "Checks player for <permission>")
	public void userCheckPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		worldName = getSafeWorldName(worldName, userName);

		String permission = user.getMatchingExpression(args.get("permission"), worldName);

		if (permission == null) {
			PermissionsEx.sendChatToPlayer(sender, "Player \"" + userName + "\" don't such have no permission");
		} else {
			PermissionsEx.sendChatToPlayer(sender, "Player \"" + userName + "\" have \"" + permission + "\" = " + user.explainExpression(permission));
		}
	}

	@Command(name = "pex", syntax = "user <user> get <option> [world]", permission = "permissions.manage.<user>", description = "Toggle debug only for <user>")
	public void userGetOption(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		worldName = getSafeWorldName(worldName, userName);

		String value = user.getOption(args.get("option"), worldName, null);

		PermissionsEx.sendChatToPlayer(sender, "Player " + userName + " @ " + worldName + " option \"" + args.get("option") + "\" = \"" + value + "\"");
	}

	@Command(name = "pex", syntax = "user <user> delete", permission = "permissions.manage.users.<user>", description = "Remove <user>")
	public void userDelete(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		if (user.isVirtual()) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User is virtual");
		}

		user.remove();

		PermissionsEx.getPermissionManager().resetUser(userName);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "User \"" + user.getName() + "\" removed!");
	}

	@Command(name = "pex", syntax = "user <user> add <permission> [world]", permission = "permissions.manage.users.permissions.<user>", description = "Add <permission> to <user> in [world]")
	public void userAddPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		user.addPermission(args.get("permission"), worldName);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Permission \"" + args.get("permission") + "\" added!");

		informPlayer(plugin, userName, "Your permissions have been changed!");
	}

	@Command(name = "pex", syntax = "user <user> remove <permission> [world]", permission = "permissions.manage.users.permissions.<user>", description = "Remove permission from <user> in [world]")
	public void userRemovePermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		String permission = this.autoCompletePermission(user, args.get("permission"), worldName);

		user.removePermission(permission, worldName);
		user.removeTimedPermission(permission, worldName);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Permission \"" + permission + "\" removed!");
		informPlayer(plugin, userName, "Your permissions have been changed!");
	}

	@Command(name = "pex", syntax = "user <user> swap <permission> <targetPermission> [world]", permission = "permissions.manage.users.permissions.<user>", description = "Swap <permission> and <targetPermission> in permission list. Could be number or permission itself")
	public void userSwapPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		String[] permissions = user.getOwnPermissions(worldName);

		try {
			int sourceIndex = getPosition(this.autoCompletePermission(user, args.get("permission"), worldName, "permission"), permissions);
			int targetIndex = getPosition(this.autoCompletePermission(user, args.get("targetPermission"), worldName, "targetPermission"), permissions);

			String targetPermission = permissions[targetIndex];

			permissions[targetIndex] = permissions[sourceIndex];
			permissions[sourceIndex] = targetPermission;

			user.setPermissions(permissions, worldName);

			PermissionsEx.sendChatToPlayer(sender, "Permissions swapped!");
		} catch (Throwable e) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Error: " + e.getMessage());
		}
	}

	@Command(name = "pex", syntax = "user <user> timed add <permission> [lifetime] [world]", permission = "permissions.manage.users.permissions.timed.<user>", description = "Add timed <permissions> to <user> for [lifetime] seconds in [world]")
	public void userAddTimedPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		int lifetime = 0;

		if (args.containsKey("lifetime")) {
			lifetime = DateUtils.parseInterval(args.get("lifetime"));
		}

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		String permission = args.get("permission");

		user.addTimedPermission(permission, worldName, lifetime);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Timed permission \"" + permission + "\" added!");
		informPlayer(plugin, userName, "Your permissions have been changed!");

		logger.info("User " + userName + " get timed permission \"" + args.get("permission") + "\" " + (lifetime > 0 ? "for " + lifetime + " seconds " : " ") + "from " + getSenderName(sender));
	}

	@Command(name = "pex", syntax = "user <user> timed remove <permission> [world]", permission = "permissions.manage.users.permissions.timed.<user>", description = "Remove timed <permission> from <user> in [world]")
	public void userRemoveTimedPermission(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));
		String permission = args.get("permission");

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		user.removeTimedPermission(args.get("permission"), worldName);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Timed permission \"" + permission + "\" removed!");
		informPlayer(plugin, userName, "Your permissions have been changed!");
	}

	@Command(name = "pex", syntax = "user <user> set <option> <value> [world]", permission = "permissions.manage.users.permissions.<user>", description = "Set <option> to <value> in [world]")
	public void userSetOption(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		user.setOption(args.get("option"), args.get("value"), worldName);

		if (args.containsKey("value") && args.get("value").isEmpty()) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Option \"" + args.get("option") + "\" cleared!");
		} else {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "Option \"" + args.get("option") + "\" set!");
		}

		informPlayer(plugin, userName, "Your permissions have been changed!");
	}

	/**
	 * User's groups management
	 */
	@Command(name = "pex", syntax = "user <user> group list [world]", permission = "permissions.manage.membership.<user>", description = "List all <user> groups")
	public void userListGroup(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		PermissionsEx.sendChatToPlayer(sender, "User " + args.get("user") + " @" + worldName + " currently in:");
		for (PermissionGroup group : user.getGroups(worldName)) {
			PermissionsEx.sendChatToPlayer(sender, "  " + group.getName());
		}
	}

	@Command(name = "pex", syntax = "user <user> group add <group> [world] [lifetime]", permission = "permissions.manage.membership.<group>", description = "Add <user> to <group>")
	public void userAddGroup(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		if (args.containsKey("lifetime")) {
			try {
				int lifetime = DateUtils.parseInterval(args.get("lifetime"));

				user.addGroup(groupName, worldName, lifetime);
			} catch (NumberFormatException e) {
				PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Group lifetime should be number!");
				return;
			}

		} else {
			user.addGroup(groupName, worldName);
		}

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "User added to group \"" + groupName + "\"!");
		informPlayer(plugin, userName, "You are assigned to \"" + groupName + "\" group");
	}

	@Command(name = "pex", syntax = "user <user> group set <group> [world]", permission = "", description = "Set <group> for <user>")
	public void userSetGroup(Object plugin, ICommandSender sender, Map<String, String> args) {
		PermissionManager manager = PermissionsEx.getPermissionManager();

		PermissionUser user = manager.getUser(this.autoCompletePlayerName(args.get("user")));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		String groupName = args.get("group");

		PermissionGroup[] groups;

		if (groupName.contains(",")) {
			String[] groupsNames = groupName.split(",");
			groups = new PermissionGroup[groupsNames.length];

			for (int i = 0; i < groupsNames.length; i++) {
				if (sender instanceof EntityPlayer && !manager.has((EntityPlayer) sender, "permissions.manage.membership." + groupsNames[i].toLowerCase())) {
					PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Don't have enough permission for group " + groupsNames[i]);
					return;
				}

				groups[i] = manager.getGroup(this.autoCompleteGroupName(groupsNames[i]));
			}

		} else {
			groupName = this.autoCompleteGroupName(groupName);

			if (groupName != null) {
				groups = new PermissionGroup[] { manager.getGroup(groupName) };

				if (sender instanceof EntityPlayer && !manager.has((EntityPlayer) sender, "permissions.manage.membership." + groupName.toLowerCase())) {
					PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Don't have enough permission for group " + groupName);
					return;
				}

			} else {
				PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "No groups set!");
				return;
			}
		}

		if (groups.length > 0) {
			user.setGroups(groups, worldName);
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "User groups set!");
		} else {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "No groups set!");
		}

		informPlayer(plugin, user.getName(), "You are now only in \"" + groupName + "\" group");
	}

	@Command(name = "pex", syntax = "user <user> group remove <group> [world]", permission = "permissions.manage.membership.<group>", description = "Remove <user> from <group>")
	public void userRemoveGroup(Object plugin, ICommandSender sender, Map<String, String> args) {
		String userName = this.autoCompletePlayerName(args.get("user"));
		String groupName = this.autoCompleteGroupName(args.get("group"));
		String worldName = this.autoCompleteWorldName(args.get("world"));

		PermissionUser user = PermissionsEx.getPermissionManager().getUser(userName);

		if (user == null) {
			PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "User does not exist");
			return;
		}

		user.removeGroup(groupName, worldName);

		PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.WHITE + "User removed from group " + groupName + "!");

		informPlayer(plugin, userName, "You were removed from \"" + groupName + "\" group");
	}

	@Command(name = "pex", syntax = "users cleanup <group> [threshold]", permission = "permissions.manage.users.cleanup", description = "Clean users of specified group, which last login was before threshold (in days). By default threshold is 30 days.")
	public void usersCleanup(Object plugin, ICommandSender sender, Map<String, String> args) {
		long threshold = 2304000;

		PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(args.get("group"));

		if (args.containsKey("threshold")) {
			try {
				threshold = Integer.parseInt(args.get("threshold")) * 86400; // 86400
																				// -
																				// seconds
																				// in
																				// one
																				// day
			} catch (NumberFormatException e) {
				PermissionsEx.sendChatToPlayer(sender, EnumChatFormatting.RED + "Threshold should be number (in days)");
				return;
			}
		}

		int removed = 0;

		Long deadline = System.currentTimeMillis() / 1000L - threshold;
		for (PermissionUser user : group.getUsers()) {
			int lastLogin = user.getOwnOptionInteger("last-login-time", null, 0);

			if (lastLogin > 0 && lastLogin < deadline) {
				user.remove();
				removed++;
			}
		}

		PermissionsEx.sendChatToPlayer(sender, "Cleaned " + removed + " users");
	}
}
