package forgeperms.impl;

import com.esotericsoftware.reflectasm.MethodAccess;

import forgeperms.api.IPermissionManager;

/**
 * A WIP SuperPerms Bridge
 * @author Joe Goett
 */
public class SuperPermsBridge implements IPermissionManager {
	MethodAccess serverAccess, playerAccess;
	int getServerIndex, getPlayerExactIndex, hasPermissionIndex;
	Object serverObj;
	
	String error = "Unknown";
	
	@Override
	public String getName() {
		return "SuperPerms Bridge";
	}

	@Override
	public boolean load() {
		try {
			serverAccess = MethodAccess.get(Class.forName("org.bukkit.Bukkit"));
			getServerIndex = serverAccess.getIndex("getServer");
			getPlayerExactIndex = serverAccess.getIndex("getPlayerExact", String.class);
			serverObj = serverAccess.invoke(null, getServerIndex);
			
			playerAccess = MethodAccess.get(Class.forName("org.bukkit.entity.Player"));
			hasPermissionIndex = playerAccess.getIndex("hasPermission", String.class);
		} catch (ClassNotFoundException e) {
			error = "Didn't find the required Bukkit stuffs! " + e.getMessage();
			return false;
		}
		return true;
	}

	@Override
	public String getLoadError() {
		return error;
	}

	@Override
	public boolean canAccess(String player, String world, String node) {
		Object playerObj = serverAccess.invoke(serverObj, getPlayerExactIndex, player);
		return (Boolean) playerAccess.invoke(playerObj, hasPermissionIndex, node);
	}

	@Override
	public boolean addGroup(String player, String group) {
		return false;
	}

	@Override
	public boolean removeGroup(String player, String group) {
		return false;
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