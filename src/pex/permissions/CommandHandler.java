package pex.permissions;

import pex.permissions.bukkit.PermissionsEx;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;

public class CommandHandler extends CommandBase {
    @Override
    public String getCommandName() {
        return "pex";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        if (par1ICommandSender instanceof MinecraftServer) {
            return true;
        }

        if (par1ICommandSender instanceof EntityPlayer) {
        	EntityPlayer pl = (EntityPlayer) par1ICommandSender;
            return PermissionsEx.instance.has(pl, "permissions.manage.command");
        } else if (par1ICommandSender instanceof TileEntityCommandBlock ) {
        	return true;
        } else {
        	return false;
        }
    }
        

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        PermissionsEx.instance.onCommand(sender, this, getCommandName(), args);
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        // TODO Auto-generated method stub
        return null;
    }
}
