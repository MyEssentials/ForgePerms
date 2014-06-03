package forgeperms.api.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO Load permissions from backend
// TODO Inheritance
// TODO Serialization

/**
 * Base implementation of permission entities
 * @author Joe Goett
 */
public class PermissionEntity {
	private String name, prefix, suffix;
	private Map<String, List<String>> permissions;
	
	public PermissionEntity(String name, String prefix, String suffix) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		permissions = new HashMap<String, List<String>>();
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
	
	public List<String> getPermissions(String world) {
		return permissions.get(world);
	}
}