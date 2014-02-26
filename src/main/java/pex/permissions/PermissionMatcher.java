package pex.permissions;

public interface PermissionMatcher {

	public boolean isMatches(String expression, String permission);

}
