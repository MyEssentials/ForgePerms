package com.sperion.pex.permissions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.sperion.pex.permissions.bukkit.PermissionsEx;

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

        EntityPlayer pl = (EntityPlayer) par1ICommandSender;
        return PermissionsEx.instance.has(pl, "permissions.manage.command");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        PermissionsEx.instance.onCommand(sender, this, getCommandName(), args);
    }
}
