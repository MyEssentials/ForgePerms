package forgeperms;

import java.util.logging.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import forgeperms.api.backend.PermissionBackend;

@Mod(modid = "ForgePerms2", name = "ForgePerms 2", version = "2.0.0")
public class ForgePerms {
	@Mod.Instance("ForgePerms2")
	public static ForgePerms Instance;

	private Logger log;
	private PermissionBackend backend;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent ev) {
		log = ev.getModLog();
		// TODO Load Config
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent ev) {
		// TODO Init and load backend
		// TODO Register Commands
	}
	
	@Mod.EventHandler
	public void imc(FMLInterModComms.IMCEvent ev) {
		for (IMCMessage msg : ev.getMessages()) {
			if (msg.key.equals("registerBackend")) {
				try {
					backend = (PermissionBackend) Class.forName(msg.getStringValue()).newInstance();
				} catch(Exception ex) {
					log.warning(String.format("Failed to register %s as a backend because %s", msg.getStringValue(), ex.getMessage()));
					ex.printStackTrace();
				}
			}
		}
	}

	public PermissionBackend getBackend() {
		return backend;
	}
}