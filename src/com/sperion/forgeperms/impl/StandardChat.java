package com.sperion.forgeperms.impl;

import com.sperion.forgeperms.api.IChatManager;
import com.sperion.forgeperms.api.IPermissionManager;
import com.sperion.pex.permissions.IPermissionEntity;
import com.sperion.pex.permissions.IPermissions;
import com.sperion.pex.permissions.PermissionUser;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class StandardChat implements IChatManager {
    public String loadError = "Unknown";
    int pexOn = 0;
    IPermissions pex = null;
    
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
    public String getName() {
        return "StandardChat";
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

    @Override
    public String getLoadError() {
        return loadError;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        if (!pexAvailable()) {
            return "";
        }

        return pex.prefix(player, world);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        if (!pexAvailable()) {
            return "";
        }

        return pex.prefix(player, world);
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        if (!pexAvailable()) {
            return;
        }
        
        pex.getUser(player).setPrefix(prefix, world);
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        if (!pexAvailable()) {
            return;
        }
        
        pex.getUser(player).setSuffix(suffix, world);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        if (!pexAvailable()) {
            return "";
        }
        
        return pex.getGroup(group).getPrefix(world);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        if (!pexAvailable()) {
            return "";
        }
        
        return pex.getGroup(group).getSuffix(world);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        if (!pexAvailable()) {
            return;
        }
        
        pex.getGroup(group).setPrefix(prefix, world);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        if (!pexAvailable()) {
            return;
        }

        pex.getGroup(group).setSuffix(suffix, world);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        if (!pexAvailable()) {
            return false;
        }
        
        return ((PermissionUser)pex.getUser(player)).inGroup(group, world);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        if (!pexAvailable()) {
            return null;
        }
        
        return ((PermissionUser)pex.getUser(player)).getGroupsNames(world);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        if (!pexAvailable()) {
            return "";
        }

        PermissionUser user = (PermissionUser) pex.getUser(player);
        if (user == null) {
            return null;
        } else if (user.getGroupsNames(world).length > 0) {
            return user.getGroupsNames(world)[0];
        } else {
            return null;
        }
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
}