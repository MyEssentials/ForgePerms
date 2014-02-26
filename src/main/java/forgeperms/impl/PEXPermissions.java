package forgeperms.impl;

import pex.permissions.IPermissions;
import pex.permissions.PermissionGroup;
import pex.permissions.PermissionUser;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import forgeperms.api.IPermissionManager;

/**
 * Permission handler for ForgePEX
 * 
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
					// Log.info("Found PEx");
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
	public boolean canAccess(String player, String world, String node) {
		if (!pexAvailable()) {
			throw new RuntimeException("PEX not found");
		}

		return pex.has(player, node, world);
	}

	@Override
	public boolean addGroup(String playerName, String groupName) {
		PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
		PermissionUser user = (PermissionUser) pex.getUser(playerName);

		if (group == null || user == null) {
			return false;
		} else {
			user.addGroup(group);
			return true;
		}
	}

	@Override
	public boolean removeGroup(String playerName, String groupName) {
		PermissionGroup group = (PermissionGroup) pex.getGroup(groupName);
		PermissionUser user = (PermissionUser) pex.getUser(playerName);

		if (group == null || user == null) {
			return false;
		} else {
			user.removeGroup(group);
			return true;
		}
	}

	@Override
	public String[] getGroupNames(String playerName) {
		PermissionUser user = (PermissionUser) pex.getUser(playerName);
		if (user == null)
			return null;
		return user.getGroupsNames();
	}

	@Override
	public String getPrimaryGroup(String world, String playerName) {
		PermissionUser user = (PermissionUser) pex.getUser(playerName);
		if (user == null)
			return null;
		return user.getGroupsNames(world)[0];
	}
}