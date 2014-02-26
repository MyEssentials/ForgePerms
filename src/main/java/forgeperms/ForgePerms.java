package forgeperms;

import java.io.File;
import java.lang.reflect.Method;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import forgeperms.api.IChatManager;
import forgeperms.api.IEconomyManager;
import forgeperms.api.IPermissionManager;
import forgeperms.impl.ItemEconomy;
import forgeperms.impl.LastResortChat;
import forgeperms.impl.LastResortPerms;
import forgeperms.impl.PEXPermissions;
import forgeperms.impl.StandardChat;

@Mod(modid = "ForgePerms", name = "ForgePerms", version = "@VERSION@.@BUILD_NUMBER@")
@NetworkMod(clientSideRequired = false, serverSideRequired = true)
public class ForgePerms {
	private static IPermissionManager permissionsHandler = null;
	private static IChatManager chatManager = null;
	private static IEconomyManager economyManager = null;
	public static String MOD_NAME = "ForgePerms";
	public static File sourceFile;

	public static void registerPermissionManager(IPermissionManager permManager) {
		Log.info("Permission manager %s being registered", permManager.getName());
		if (ForgePerms.permissionsHandler == null) {
			if (permManager.load()) {
				ForgePerms.permissionsHandler = permManager;
			} else {
				Log.info("%s failed to load because %s", permManager.getName(), permManager.getLoadError());
			}
		} else {
			Log.info("Permission Manager(%s) already loaded. Did not load %s", ForgePerms.permissionsHandler.getName(), permManager.getName());
		}
	}

	public static void registerChatManager(IChatManager chatManager) {
		Log.info("Chat manager %s being registered", chatManager.getName());
		if (ForgePerms.chatManager == null) {
			if (chatManager.load()) {
				ForgePerms.chatManager = chatManager;
			} else {
				Log.info("%s failed to load because %s", chatManager.getName(), chatManager.getLoadError());
			}
		} else {
			Log.info("Chat Manager(%s) already loaded. Did not load %s", ForgePerms.chatManager.getName(), chatManager.getName());
		}
	}

	public static void registerEconomyManager(IEconomyManager economyManager) {
		Log.info("Economy manager %s being registered", economyManager.getName());
		if (ForgePerms.economyManager == null) {
			if (economyManager.load()) {
				ForgePerms.economyManager = economyManager;
			} else {
				Log.info("%s failed to load because %s", economyManager.getName(), economyManager.getLoadError());
			}
		} else {
			Log.info("Economy Manager(%s) already loaded. Did not load %s", ForgePerms.economyManager.getName(), economyManager.getName());
		}
	}

	public static IPermissionManager getPermissionManager() {
		return permissionsHandler;
	}

	public static IChatManager getChatManager() {
		return chatManager;
	}

	public static IEconomyManager getEconomyManager() {
		return economyManager;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent ev) {
		sourceFile = ev.getSourceFile();
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		ForgePerms.registerPermissionManager(new PEXPermissions());
		ForgePerms.registerChatManager(new StandardChat());
		ForgePerms.registerEconomyManager(new ItemEconomy());

		ForgePerms.registerPermissionManager(new LastResortPerms());
		ForgePerms.registerChatManager(new LastResortChat());

		// MinecraftForge.EVENT_BUS.register(new CommandHandler());
	}

	private void processIMC(IMCMessage msg){
		try {
			Class<?> reflectClass = Class.forName(msg.getStringValue());
			Method reflectMethod = reflectClass.getMethod("setupManagers", IChatManager.class, IEconomyManager.class, IPermissionManager.class);
			reflectMethod.invoke(null, chatManager, economyManager, permissionsHandler);
			
			Log.info("Successfully setup %s", msg.getStringValue());
		} catch (ClassNotFoundException e) {
			Log.warning("Failed to get class %s", msg.getStringValue());
		} catch (NoSuchMethodException e) {
			Log.warning("Failed to get method setupManagers(IChatManager, IEconomyManager, IPermissionManager)");
		} catch (SecurityException e) {
			Log.warning(String.format("Exception while trying to access the method : %s", e.toString()));
		} catch (Exception e) {
			Log.warning("Unknown error occured while setting up %s", msg.getStringValue());
		}
	}
	
	@EventHandler
	public void imc(IMCEvent ev){
		for (IMCMessage msg : ev.getMessages()){
			processIMC(msg);
		}
	}
}