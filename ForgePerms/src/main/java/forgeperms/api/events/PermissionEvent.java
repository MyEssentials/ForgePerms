package forgeperms.api.events;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import forgeperms.api.entities.PermissionEntity;

// TODO More events? (Option/meta value changed, inheritance changed?)

public class PermissionEvent extends Event {
	public PermissionEntity entity;
	public String permission;
	
	public PermissionEvent(PermissionEntity entity, String permission) {
		this.entity = entity;
		this.permission = permission;
	}
	
	/**
	 * Checks if the PermissionEntity has the requested permission. Event will be canceled if the user does NOT have the permission.
	 * 
	 * Should not be used often!
	 * 
	 * @author Joe Goett
	 */
	@Cancelable
	public static class HasPermission extends PermissionEvent {
		public HasPermission(PermissionEntity entity, String permission) {
			super(entity, permission);
		}
	}
	
	/**
	 * Is fired off whenever a permission has changed for the entity
	 * 
	 * Possible uses: Permission check cache for highly used permission
	 * 
	 * @author Joe Goett
	 */
	public static class PermissionChanged extends PermissionEvent {
		public PermissionChanged(PermissionEntity entity, String permission) {
			super(entity, permission);
		}
	}
}