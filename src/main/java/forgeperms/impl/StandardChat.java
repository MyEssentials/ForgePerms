package forgeperms.impl;

import pex.permissions.IPermissions;
import pex.permissions.PermissionGroup;
import pex.permissions.PermissionUser;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import forgeperms.Log;
import forgeperms.api.IChatManager;
import forgeperms.api.IPermissionManager;

public class StandardChat implements IChatManager {
    public String loadError = "Unknown";
    int pexOn = 0;
    IPermissions pex = null;

    private boolean pexAvailable() {
        if (pexOn == 0) {
            for (ModContainer cont : Loader.instance().getModList()) {
                if (cont.getModId().equalsIgnoreCase("PermissionsEx")) {
                    //Log.info("Found PEx");
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

        return pex.suffix(player, world);
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
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        if (!pexAvailable()) {
            return defaultValue;
        }
        
        PermissionUser user = (PermissionUser) pex.getUser(playerName);
        if (user == null) return defaultValue;
        return user.getOption(node, world, defaultValue);
    }

    @Override
    public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
        if (!pexAvailable()) {
            return defaultValue;
        }
        
        PermissionUser user = (PermissionUser) pex.getUser(playerName);
        if (user == null) return defaultValue;
        return user.getOptionInteger(node, world, defaultValue);
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        if (!pexAvailable()) {
            return defaultValue;
        }
        
        PermissionUser user = (PermissionUser) pex.getUser(playerName);
        if (user == null) return defaultValue;
        return user.getOptionDouble(node, world, defaultValue);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        if (!pexAvailable()) {
            return defaultValue;
        }
        
        PermissionUser user = (PermissionUser) pex.getUser(playerName);
        if (user == null) return defaultValue;
        return user.getOptionBoolean(node, world, defaultValue);
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        if (!pexAvailable()) {
            return;
        }
        
        PermissionUser user = (PermissionUser) pex.getUser(playerName);
        if (user == null) return;
        user.setOption(node, value, world);
    }

    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        if (!pexAvailable()) {
            return;
        }
        
        PermissionUser user = (PermissionUser) pex.getUser(playerName);
        if (user == null) return;
        user.setOption(node, String.valueOf(value), world);
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        if (!pexAvailable()) {
            return;
        }
        
        PermissionUser user = (PermissionUser) pex.getUser(playerName);
        if (user == null) return;
        user.setOption(node, String.valueOf(value), world);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        if (!pexAvailable()) {
            return;
        }
        
        PermissionUser user = (PermissionUser) pex.getUser(playerName);
        if (user == null) return;
        user.setOption(node, String.valueOf(value), world);
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        if (!pexAvailable()) {
            return defaultValue;
        }
        
        PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
        if (group == null) return defaultValue;
        return group.getOption(node, world, defaultValue);
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        if (!pexAvailable()) {
            return defaultValue;
        }
        
        PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
        if (group == null) return defaultValue;
        return group.getOptionInteger(node, world, defaultValue);
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        if (!pexAvailable()) {
            return defaultValue;
        }
        
        PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
        if (group == null) return defaultValue;
        return group.getOptionDouble(node, world, defaultValue);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        if (!pexAvailable()) {
            return defaultValue;
        }
        
        PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
        if (group == null) return defaultValue;
        return group.getOptionBoolean(node, world, defaultValue);
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        if (!pexAvailable()) {
            return;
        }
        
        PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
        if (group == null) return;
        group.setOption(node, value, world);
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        if (!pexAvailable()) {
            return;
        }
        
        PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
        if (group == null) return;
        group.setOption(node, String.valueOf(value), world);
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        if (!pexAvailable()) {
            return;
        }
        
        PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
        if (group == null) return;
        group.setOption(node, String.valueOf(value), world);
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        if (!pexAvailable()) {
            return;
        }
        
        PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
        if (group == null) return;
        group.setOption(node, String.valueOf(value), world);
    }
}