package com.sperion.forgeperms;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "ForgePerms", name = "ForgePerms", version = "1.0.0")
@NetworkMod(clientSideRequired = false, serverSideRequired = true)
public class ForgePerms {
    private static PermissionsBase permissionsHandler = null;
    public static String MOD_NAME = "ForgePerms";

    public static void registerHandler(PermissionsBase permHandler) {
        if (ForgePerms.permissionsHandler == null) {
            if (permHandler.load()) {
                ForgePerms.permissionsHandler = permHandler;
            } else {
                Log.info("%s failed to load because %s", permHandler.name,
                        permHandler.loadError);
            }
        } else {
            Log.info("PermissionHandler(%s) already loaded. Did not load %s",
                    ForgePerms.permissionsHandler.name, permHandler.name);
        }
    }

    public static PermissionsBase getPermissionsHandler() {
        return permissionsHandler;
    }

    @Mod.ServerStarted
    public void serverStarted(FMLServerStartedEvent event) {
        if (ForgePerms.permissionsHandler == null) {
            ForgePerms.registerHandler(new PEXPermissions());
        }
    }
}