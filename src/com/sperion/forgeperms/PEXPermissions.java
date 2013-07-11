package com.sperion.forgeperms;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.sperion.pex.permissions.IPermissionEntity;
import com.sperion.pex.permissions.IPermissions;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class PEXPermissions extends PermissionsBase {
    int pexOn = 0;
    IPermissions pex = null;

    public PEXPermissions() {
        name = "Forge PEX";
    }

    @Override
    public boolean load() {
        if (pexAvailable()) {
            return true;
        } else {
            loadError = "PermissionsEX was not found";
            return false;
        }
    }

    private boolean pexAvailable() {
        if (pexOn == 0) {
            for (ModContainer cont : Loader.instance().getModList()) {
                if (cont.getModId().equalsIgnoreCase("PermissionsEx")) {
                    if (cont.getMod() instanceof IPermissions) {
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
    public String getPrefix(String player, String world) {
        if (!pexAvailable()) {
            return "";
        }

        return pex.prefix(player, world);
    }

    @Override
    public String getPostfix(String player, String world) {
        if (!pexAvailable()) {
            return "";
        }

        return pex.suffix(player, world);
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
            return getOption(pl.username, String.valueOf(pl.dimension), node,
                    def);
        }
    }
}