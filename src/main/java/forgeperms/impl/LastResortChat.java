package forgeperms.impl;

import forgeperms.api.IChatManager;

public class LastResortChat implements IChatManager {
	@Override
	public String getName() {
		return "LastResortChat";
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
	public String getPlayerPrefix(String world, String player) {
		return "";
	}

	@Override
	public String getPlayerSuffix(String world, String player) {
		return "";
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
	}

	@Override
	public String getGroupPrefix(String world, String group) {
		return "";
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		return "";
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
	}

	@Override
	public boolean playerInGroup(String world, String player, String group) {
		return false;
	}

	@Override
	public String[] getPlayerGroups(String world, String player) {
		return null;
	}

	@Override
	public String getPrimaryGroup(String world, String player) {
		return "";
	}

	@Override
	public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
		return defaultValue;
	}

	@Override
	public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
		return defaultValue;
	}

	@Override
	public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
		return defaultValue;
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
		return defaultValue;
	}

	@Override
	public void setPlayerInfoString(String world, String playerName, String node, String value) {
	}

	@Override
	public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
	}

	@Override
	public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
	}

	@Override
	public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
	}

	@Override
	public String getGroupInfoString(String world, String group, String node, String defaultValue) {
		return defaultValue;
	}

	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
		return defaultValue;
	}

	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
		return defaultValue;
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
		return defaultValue;
	}

	@Override
	public void setGroupInfoString(String world, String group, String node, String value) {
	}

	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value) {
	}

	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value) {
	}

	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
	}
}