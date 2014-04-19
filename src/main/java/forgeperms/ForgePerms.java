package forgeperms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import forgeperms.api.ForgePermsAPI;
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
	public static String MOD_NAME = "ForgePerms";
	public static File sourceFile;

	public static void registerPermissionManager(IPermissionManager permManager) {
		Log.info("Permission manager %s being registered", permManager.getName());
		if (ForgePermsAPI.permManager == null) {
			if (permManager.load()) {
				ForgePermsAPI.permManager = permManager;
			} else {
				Log.info("%s failed to load because %s", permManager.getName(), permManager.getLoadError());
			}
		} else {
			Log.info("Permission Manager(%s) already loaded. Did not load %s", ForgePermsAPI.permManager.getName(), permManager.getName());
		}
	}

	public static void registerChatManager(IChatManager chatManager) {
		Log.info("Chat manager %s being registered", chatManager.getName());
		if (ForgePermsAPI.chatManager == null) {
			if (chatManager.load()) {
				ForgePermsAPI.chatManager = chatManager;
			} else {
				Log.info("%s failed to load because %s", chatManager.getName(), chatManager.getLoadError());
			}
		} else {
			Log.info("Chat Manager(%s) already loaded. Did not load %s", ForgePermsAPI.chatManager.getName(), chatManager.getName());
		}
	}

	public static void registerEconomyManager(IEconomyManager economyManager) {
		Log.info("Economy manager %s being registered", economyManager.getName());
		if (ForgePermsAPI.econManager == null) {
			if (economyManager.load()) {
				ForgePermsAPI.econManager = economyManager;
			} else {
				Log.info("%s failed to load because %s", economyManager.getName(), economyManager.getLoadError());
			}
		} else {
			Log.info("Economy Manager(%s) already loaded. Did not load %s", ForgePermsAPI.econManager.getName(), economyManager.getName());
		}
	}

	public static IPermissionManager getPermissionManager() {
		return ForgePermsAPI.permManager;
	}

	public static IChatManager getChatManager() {
		return ForgePermsAPI.chatManager;
	}

	public static IEconomyManager getEconomyManager() {
		return ForgePermsAPI.econManager;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent ev) {
		sourceFile = ev.getSourceFile();
	}
	
	@Mod.EventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent ev) {
		File pluginsFolder = new File("./plugins");
		if (ev.getServer().getServerModName().contains("mcpc") && pluginsFolder.exists()) {
			injectBukkitBridge(sourceFile, pluginsFolder);
		}
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		ForgePerms.registerPermissionManager(new PEXPermissions());
		ForgePerms.registerChatManager(new StandardChat());
		ForgePerms.registerEconomyManager(new ItemEconomy());

		ForgePerms.registerPermissionManager(new LastResortPerms());
		ForgePerms.registerChatManager(new LastResortChat());
	}
	
	private void injectBukkitBridge(File self, File pluginFolder) {
		Log.info("Injecting ForgePermsCBBridge!");
		try {
			ZipFile zip = new ZipFile(self);
			ZipEntry entry = zip.getEntry("ForgePermsCBBridge.jar");
			if (entry == null) {
				Log.severe("Mod doesn't contain ForgePermsCBBridge! If using MCPC, you need this!");
				zip.close();
				return;
			}
			InputStream stream = zip.getInputStream(entry);
			FileOutputStream outStream = new FileOutputStream(new File(pluginFolder, entry.getName()));
			
			byte[] tmp = new byte[4*1024];
			int size = 0;
			while ((size = stream.read(tmp)) != -1) {
				outStream.write(tmp, 0, size);
			}
			outStream.close();
			zip.close();
		} catch (Exception e) {
			Log.severe("Failed to inject ForgePermsCBBridge! ", e);
		}
		Log.info("Injected ForgePermsCBBridge successfully!");
	}
}