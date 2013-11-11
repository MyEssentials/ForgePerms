package com.sperion.forgeperms;

import com.sperion.forgeperms.api.IChatManager;
import com.sperion.forgeperms.api.IEconomyManager;
import com.sperion.forgeperms.api.IPermissionManager;
import com.sperion.forgeperms.impl.ItemEconomy;
import com.sperion.forgeperms.impl.LastResortChat;
import com.sperion.forgeperms.impl.LastResortPerms;
import com.sperion.forgeperms.impl.PEXPermissions;
import com.sperion.forgeperms.impl.StandardChat;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "ForgePerms", name = "ForgePerms", version = "1.0.0")
@NetworkMod(clientSideRequired = false, serverSideRequired = true)
public class ForgePerms {
    private static IPermissionManager permissionsHandler = null;
    private static IChatManager chatManager = null;
    private static IEconomyManager economyManager = null;
    public static String MOD_NAME = "ForgePerms";

    public static void registerPermissionManager(IPermissionManager permManager) {
        Log.info("Permission manager %s being registered", permManager.getName());
        if (ForgePerms.permissionsHandler == null) {
            if (permManager.load()) {
                ForgePerms.permissionsHandler = permManager;
            } else {
                Log.info("%s failed to load because %s", permManager.getName(), permManager.getLoadError());
            }
        } else {
            Log.info("Permission Manager(%s) already loaded. Did not load %s", ForgePerms.permissionsHandler.getName(), permManager.getName());
        }
    }
    
    public static void registerChatManager(IChatManager chatManager){
        Log.info("Chat manager %s being registered", chatManager.getName());
        if (ForgePerms.chatManager == null) {
            if (chatManager.load()) {
                ForgePerms.chatManager = chatManager;
            } else {
                Log.info("%s failed to load because %s", chatManager.getName(), chatManager.getLoadError());
            }
        } else {
            Log.info("Chat Manager(%s) already loaded. Did not load %s", ForgePerms.chatManager.getName(), chatManager.getName());
        }
    }
    
    public static void registerEconomyManager(IEconomyManager economyManager){
        Log.info("Economy manager %s being registered", economyManager.getName());
        if (ForgePerms.economyManager == null) {
            if (economyManager.load()) {
                ForgePerms.economyManager = economyManager;
            } else {
                Log.info("%s failed to load because %s", economyManager.getName(), economyManager.getLoadError());
            }
        } else {
            Log.info("Economy Manager(%s) already loaded. Did not load %s", ForgePerms.economyManager.getName(), economyManager.getName());
        }
    }

    public static IPermissionManager getPermissionManager() {
        return permissionsHandler;
    }

    public static IChatManager getChatManager() {
        return chatManager;
    }

    public static IEconomyManager getEconomyManager() {
        return economyManager;
    }

    @Mod.ServerStarted
    public void serverStarted(FMLServerStartedEvent event) {
        ForgePerms.registerPermissionManager(new PEXPermissions());
        ForgePerms.registerChatManager(new StandardChat());
        ForgePerms.registerEconomyManager(new ItemEconomy());

        ForgePerms.registerPermissionManager(new LastResortPerms());
        ForgePerms.registerChatManager(new LastResortChat());
    }
}