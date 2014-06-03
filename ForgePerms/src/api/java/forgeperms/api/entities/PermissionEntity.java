package forgeperms.api.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of permission entities
 * @author Joe Goett
 */
public class PermissionEntity {
	private String name, prefix, suffix;
	private List<String> permissions;
	
	public PermissionEntity(String name, String prefix, String suffix) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		permissions = new ArrayList<String>();
	}
	
	public String getName() {
		return name;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
}