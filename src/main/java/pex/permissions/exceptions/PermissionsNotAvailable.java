package pex.permissions.exceptions;

@SuppressWarnings("serial")
public class PermissionsNotAvailable extends RuntimeException {

    public PermissionsNotAvailable() {
        super(
                "Permissions manager is not accessable. Is the PermissionsEx plugin enabled?");
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
