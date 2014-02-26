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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import org.bukkit.ChatColor;

import pex.permissions.PermissionManager;
import pex.permissions.bukkit.PermissionsEx;
import pex.permissions.commands.Command;
import pex.utils.StringUtils;

public class WorldCommands extends PermissionsCommand {

	@Command(name = "pex", syntax = "worlds", description = "Print loaded worlds", isPrimary = true, permission = "permissions.manage.worlds")
	public void worldsTree(Object plugin, ICommandSender sender, Map<String, String> args) {
		WorldServer[] worlds = MinecraftServer.getServer().worldServers;

		PermissionManager manager = PermissionsEx.getPermissionManager();

		PermissionsEx.sendChatToPlayer(sender, "Worlds on server: ");
		for (WorldServer world : worlds) {
			String[] parentWorlds = manager.getWorldInheritance(String.valueOf(world.provider.dimensionId));
			String output = "  " + String.valueOf(world.provider.dimensionId);
			if (parentWorlds.length > 0) {
				output += ChatColor.GREEN + " [" + ChatColor.WHITE + StringUtils.implode(parentWorlds, ", ") + ChatColor.GREEN + "]";
			}

			PermissionsEx.sendChatToPlayer(sender, output);
		}
	}

	@Command(name = "pex", syntax = "world <world>", description = "Print <world> inheritance info", permission = "permissions.manage.worlds")
	public void worldPrintInheritance(Object plugin, ICommandSender sender, Map<String, String> args) {
		String worldName = this.autoCompleteWorldName(args.get("world"));
		PermissionManager manager = PermissionsEx.getPermissionManager();

		/*
		 * if (Bukkit.getServer().getWorld(worldName) == null) {
		 * PermissionsEx.sendChatToPlayer(sender, "Specified world \"" +
		 * args.get("world") + "\" not found."); return; }
		 */

		String[] parentWorlds = manager.getWorldInheritance(worldName);

		PermissionsEx.sendChatToPlayer(sender, "World " + worldName + " inherit:");
		if (parentWorlds.length == 0) {
			PermissionsEx.sendChatToPlayer(sender, "nothing :3");
			return;
		}

		for (String parentWorld : parentWorlds) {
			// String[] parents = manager.getWorldInheritance(parentWorld);
			String output = "  " + parentWorld;
			if (parentWorlds.length > 0) {
				output += ChatColor.GREEN + " [" + ChatColor.WHITE + StringUtils.implode(parentWorlds, ", ") + ChatColor.GREEN + "]";
			}

			PermissionsEx.sendChatToPlayer(sender, output);
		}
	}

	@Command(name = "pex", syntax = "world <world> inherit <parentWorlds>", description = "Set <parentWorlds> for <world>", permission = "permissions.manage.worlds.inheritance")
	public void worldSetInheritance(Object plugin, ICommandSender sender, Map<String, String> args) {
		String worldName = this.autoCompleteWorldName(args.get("world"));
		PermissionManager manager = PermissionsEx.getPermissionManager();
		/*
		 * if (Bukkit.getServer().getWorld(worldName) == null) {
		 * PermissionsEx.sendChatToPlayer(sender, "Specified world \"" +
		 * args.get("world") + "\" not found."); return; }
		 */

		List<String> parents = new ArrayList<String>();
		String parentWorlds = args.get("parentWorlds");
		if (parentWorlds.contains(",")) {
			for (String world : parentWorlds.split(",")) {
				world = this.autoCompleteWorldName(world, "parentWorlds");
				if (!parents.contains(world)) {
					parents.add(world.trim());
				}
			}
		} else {
			parents.add(parentWorlds.trim());
		}

		manager.setWorldInheritance(worldName, parents.toArray(new String[0]));

		PermissionsEx.sendChatToPlayer(sender, "World " + worldName + " inherits " + StringUtils.implode(parents, ", "));
	}
}
