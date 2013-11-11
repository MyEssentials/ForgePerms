package com.sperion.forgeperms.impl;

import com.sperion.forgeperms.api.IChatManager;

public class LastResortChat implements IChatManager{
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
	public String getOption(String player, String world, String node, String def) {
		return "";
	}
}