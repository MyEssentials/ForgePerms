package forgeperms.api.entities;

// TODO Link users to groups for inheritance (Maybe have it done in PermissionEntity?)

public class PermissionUser extends PermissionEntity {
	public PermissionUser(String name, String prefix, String suffix) {
		super(name, prefix, suffix);
	}
}