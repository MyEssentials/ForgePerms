package forgeperms.impl;

import forgeperms.api.IPermissionManager;

public class LastResortPerms implements IPermissionManager {

	@Override
	public String getName() {
		return "LastResortPerms";
	}

	@Override
	public boolean load() {
		return true;
	}

	@Override
	public String getLoadError() {
		return "";
	}

	@Override
	public boolean canAccess(String name, String world, String node) {
		return false;
	}

    @Override
    public boolean addGroup(String player, String group) {
        return true;
    }

    @Override
    public boolean removeGroup(String player, String group) {
        return true;
    }

    @Override
    public String[] getGroupNames(String player) {
        return null;
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        return null;
    }
}
