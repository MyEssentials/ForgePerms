package forgeperms.api.backend;

import forgeperms.api.entities.PermissionGroup;
import forgeperms.api.entities.PermissionUser;
import net.minecraft.entity.player.EntityPlayer;

// TODO Finish! :p

public abstract class PermissionBackend {
	/**
	 * Returns the PermissionUser with the given username
	 * @param username
	 * @return
	 */
	public abstract PermissionUser getUser(String username);
	
	/**
	 * Returns the PermissionUser of the given EntityPlayer
	 * @param player
	 * @return
	 */
	public PermissionUser getUser(EntityPlayer player) {
		return getUser(player.getCommandSenderName());
	}
	
	/**
	 * Returns the PermissionGroup with the given name
	 * @param name
	 * @return
	 */
	public abstract PermissionGroup getGroup(String name);
}