package com.sperion.forgeperms.impl;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.sperion.forgeperms.api.IPermissionManager;
import com.sperion.pex.permissions.IPermissionEntity;
import com.sperion.pex.permissions.IPermissions;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

/**
 * Permission handler for ForgePEX
 * @author Joe Goett
 */
public class PEXPermissions implements IPermissionManager {
    public String loadError = "Unknown";
    int pexOn = 0;
    IPermissions pex = null;

    @Override
    public boolean load() {
        if (pexAvailable()) {
            return true;
        } else {
            loadError = "PermissionsEX was not found";
            return false;
        }
    }

    @Override
    public String getLoadError() {
        return loadError;
    }

    @Override
    public String getName() {
        return "Forge PEX";
    }

    private boolean pexAvailable() {
        if (pexOn == 0) {
            for (ModContainer cont : Loader.instance().getModList()) {
                if (cont.getModId().equalsIgnoreCase("PermissionsEx")) {
                    if (cont.getMod() instanceof IPermissionManager) {
                        pex = (IPermissions) cont.getMod();
                    }

                    break;
                }
            }
            pexOn = pex == null ? 2 : 1;
        }

        return pexOn == 1;
    }

    @Override
    public boolean canAccess(String name, String world, String node) {
        if (!pexAvailable()) {
            throw new RuntimeException("PEX not found");
        }

        return pex.has(name, node, world);
    }

    @Override
    public String getOption(String player, String world, String node, String def) {
        if (!pexAvailable()) {
            return def;
        }

        IPermissionEntity entity = pex.getUser(player);
        if (entity == null) {
            return def;
        }

        return entity.getOption(node, world, def);
    }

    @Override
    public String getOption(ICommandSender name, String node, String def) {
        if (!(name instanceof EntityPlayer)) {
            return def;
        } else {
            EntityPlayer pl = (EntityPlayer) name;
            return getOption(pl.username, String.valueOf(pl.dimension), node, def);
        }
    }
}