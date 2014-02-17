package forgeperms;

import com.google.common.base.Joiner;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class CommandHandler {
	@ForgeSubscribe
	public void commandEvent(CommandEvent ev){
		if (!ev.isCancelable() || ev.isCanceled() || MinecraftServer.getServer().getServerModName().contains("mcpc") || MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(ev.sender.getCommandSenderName()) || ev.sender instanceof MinecraftServer){
			return;
		}
		
		if (ev.command.getClass().getName().contains("mytown.cmd")) return;  //Ignore MyTown commands (will probably make a better way later)
		
		if (!ForgePerms.getPermissionManager().canAccess(ev.sender.getCommandSenderName(), ev.sender.getEntityWorld().provider.getDimensionName(), ev.command.getClass().getName() + (ev.parameters.length <= 0 ? "" : "." + Joiner.on('.').join(ev.parameters)))){
			ev.setCanceled(true);
			ev.sender.sendChatToPlayer(ChatMessageComponent.createFromText("You do not have the permission to use this command!"));
		}
	}
}