package forgeperms.api;

/**
 * Copy this into another package and make it your own. setupManagers is required!
 * @author Joe Goett
 */
public class IForgePerms {
	public static IForgePerms instance;
	
	public IChatManager chatManager;
	public IEconomyManager econManager;
	public IPermissionManager permManager;
	
	/**
	 * Sets up the different managers. Called directly by ForgePerms
	 * @param chatMan
	 * @param econMan
	 * @param permMan
	 */
	public static void setupManagers(IChatManager chatMan, IEconomyManager econMan, IPermissionManager permMan){
		instance = new IForgePerms();
		instance.chatManager = chatMan;
		instance.econManager = econMan;
		instance.permManager = permMan;
	}
}