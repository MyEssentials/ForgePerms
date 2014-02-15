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
package com.sperion.pex.permissions.bukkit;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.Configuration;

import org.bukkit.ChatColor;

import com.sperion.pex.permissions.CommandHandler;
import com.sperion.pex.permissions.IPermissions;
import com.sperion.pex.permissions.PermissionBackend;
import com.sperion.pex.permissions.PermissionGroup;
import com.sperion.pex.permissions.PermissionManager;
import com.sperion.pex.permissions.PermissionUser;
import com.sperion.pex.permissions.backends.FileBackend;
import com.sperion.pex.permissions.backends.SQLBackend;
import com.sperion.pex.permissions.bukkit.commands.GroupCommands;
import com.sperion.pex.permissions.bukkit.commands.PromotionCommands;
import com.sperion.pex.permissions.bukkit.commands.UserCommands;
import com.sperion.pex.permissions.bukkit.commands.UtilityCommands;
import com.sperion.pex.permissions.bukkit.commands.WorldCommands;
import com.sperion.pex.permissions.commands.CommandsManager;
import com.sperion.pex.permissions.exceptions.PermissionsNotAvailable;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "PermissionsEx", name = "PermissionsEx", version = "1.5.0.0")
@NetworkMod(clientSideRequired = false, serverSideRequired = true)
public class PermissionsEx implements IPermissions {
    @Mod.Instance("PermissionsEx")
    public static PermissionsEx instance;

    protected static final Logger logger = Logger.getLogger("PermissionsEx");
    protected PermissionManager permissionsManager;
    protected CommandsManager commandsManager;
    protected Configuration config;

    protected File configFile;

    // protected BukkitPermissions superms;

    public PermissionsEx() {
        logger.setParent(FMLLog.getLogger());
        PermissionBackend.registerBackendAlias("sql", SQLBackend.class);
        PermissionBackend.registerBackendAlias("file", FileBackend.class);

        logger.log(Level.INFO, "[PermissionsEx] PermissionEx plugin initialized.");
    }

    @EventHandler
    public void onLoad(FMLPreInitializationEvent ev) {
        configFile = ev.getSuggestedConfigurationFile();
        config = new Configuration(configFile);

        commandsManager = new CommandsManager(this);
        permissionsManager = new PermissionManager(config);

        config.save();
    }

    @EventHandler
    public void modsLoaded(FMLServerStartedEvent var1) {
        ServerCommandManager mgr = (ServerCommandManager) MinecraftServer
                .getServer().getCommandManager();
        mgr.registerCommand(new CommandHandler());

        onEnable();
    }

    public void onEnable() {
        if (permissionsManager == null) {
            permissionsManager = new PermissionManager(config);
            config.save();
        }

        // Register commands
        commandsManager.register(new UserCommands());
        commandsManager.register(new GroupCommands());
        commandsManager.register(new PromotionCommands());
        commandsManager.register(new WorldCommands());
        commandsManager.register(new UtilityCommands());

        // Register Player permissions cleaner
        PlayerEventsListener cleaner = new PlayerEventsListener();
        cleaner.logLastPlayerLogin = config.get("permissions", "log-players",
                cleaner.logLastPlayerLogin).getBoolean(
                cleaner.logLastPlayerLogin);
        GameRegistry.registerPlayerTracker(cleaner);

        // register service
        // this.getServer().getServicesManager().register(PermissionManager.class,
        // this.permissionsManager, this, ServicePriority.Normal);

        /*
         * ConfigurationSection dinnerpermsConfig =
         * this.config.getConfigurationSection("permissions.superperms");
         * 
         * if (dinnerpermsConfig == null) { dinnerpermsConfig =
         * this.config.createSection("permissions.superperms"); }
         * 
         * this.superms = new BukkitPermissions(this, dinnerpermsConfig);
         * 
         * this.superms.updateAllPlayers();
         */

        config.save();

        // Start timed permissions cleaner timer
        permissionsManager.initTimer();

        logger.log(Level.INFO, "[PermissionsEx] enabled");
    }

    public void onDisable() {
        if (permissionsManager != null) {
            permissionsManager.end();
        }

        // this.getServer().getServicesManager().unregister(PermissionManager.class,
        // this.permissionsManager);

        logger.log(Level.INFO, "[PermissionsEx] disabled successfully.");
    }

    public boolean onCommand(ICommandSender sender, CommandBase command,
            String commandLabel, String[] args) {
        if (args.length > 0) {
            return commandsManager.execute(sender, command, args);
        } else {
            if (sender instanceof EntityPlayer) {
                sendChatToPlayer(sender, "[" + ChatColor.RED + "PermissionsEx" + ChatColor.WHITE + "]");

                return !permissionsManager.has((EntityPlayer) sender, "permissions.manage");
            } else {
                sendChatToPlayer(sender, "[PermissionsEx]");

                return false;
            }
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public static PermissionsEx getPlugin() {
        return instance;
    }

    public static boolean isAvailable() {
        PermissionsEx plugin = getPlugin();

        return plugin instanceof PermissionsEx
                && plugin.permissionsManager != null;
    }

    public static PermissionManager getPermissionManager() {
        if (!isAvailable()) {
            throw new PermissionsNotAvailable();
        }

        return getPlugin().permissionsManager;
    }

    @Override
    public PermissionUser getUser(EntityPlayer player) {
        return getPermissionManager().getUser(player);
    }

    @Override
    public PermissionUser getUser(String name) {
        return getPermissionManager().getUser(name);
    }

    @Override
    public PermissionGroup getGroup(String name) {
        return getPermissionManager().getGroup(name);
    }

    @Override
    public boolean has(EntityPlayer player, String permission) {
        return permissionsManager.has(player, permission);
    }

    @Override
    public boolean has(EntityPlayer player, String permission, String world) {
        return permissionsManager.has(player, permission, world);
    }

    @Override
    public boolean has(String player, String permission, String world) {
        return permissionsManager.has(player, permission, world);
    }

    @Override
    public String prefix(String player, String world) {
        return permissionsManager.getUser(player).getPrefix(world);
    }

    @Override
    public String suffix(String player, String world) {
        return permissionsManager.getUser(player).getSuffix(world);
    }

    public class PlayerEventsListener implements IPlayerTracker {
        protected boolean logLastPlayerLogin = false;

        @Override
        public void onPlayerLogin(EntityPlayer event) {
            if (!logLastPlayerLogin) {
                return;
            }

            PermissionUser user = getPermissionManager().getUser(event);
            user.setOption("last-login-time", Long.toString(System.currentTimeMillis() / 1000L));
            // user.setOption("last-login-ip",
            // event.getPlayer().getAddress().getAddress().getHostAddress()); //
            // somehow this won't work
        }

        @Override
        public void onPlayerLogout(EntityPlayer event) {
            if (logLastPlayerLogin) {
                getPermissionManager().getUser(event).setOption(
                        "last-logout-time",
                        Long.toString(System.currentTimeMillis() / 1000L));
            }

            getPermissionManager().resetUser(event.username);
        }

        @Override
        public void onPlayerChangedDimension(EntityPlayer player) {}

        @Override
        public void onPlayerRespawn(EntityPlayer player) {}
    }
    
    public static void sendChatToPlayer(EntityPlayer entity, String msg){
        entity.sendChatToPlayer(ChatMessageComponent.createFromText(msg));
    }
    
    public static void sendChatToPlayer(ICommandSender sender, String msg){
        sender.sendChatToPlayer(ChatMessageComponent.createFromText(msg));
    }
}
