package forgeperms.asm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

public class FPCommandHandler extends CommandHandler {
	@Override
	public int executeCommand(ICommandSender sender, String input) {
        input = input.trim();

        if (input.startsWith("/")) {
            input = input.substring(1);
        }

        String[] args = input.split(" ");
        String commandName = args[0];

        String[] var1 = new String[args.length - 1];
        System.arraycopy(args, 1, var1, 0, args.length - 1);

        args = var1;

        ICommand icommand = (ICommand) super.getCommands().get(commandName);

        int j = 0;

        try {
            if (icommand == null) {
                throw new CommandNotFoundException();
            }
            
            if (forgeperms.api.ForgePermsAPI.permManager.canAccess(sender.getCommandSenderName(), sender.getEntityWorld().provider.getDimensionName(), icommand.getClass().getName())) {
                CommandEvent event = new CommandEvent(icommand, sender, args);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    if (event.exception != null) {
                        throw event.exception;
                    }
                    return 1;
                }

                icommand.processCommand(sender, args);
                ++j;

            } else {
            	sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.generic.permission").setColor(EnumChatFormatting.RED));
            }
        } catch (WrongUsageException wrongusageexception) {
        	sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.generic.usage", new Object[] {ChatMessageComponent.createFromTranslationWithSubstitutions(wrongusageexception.getMessage(), wrongusageexception.getErrorOjbects())}).setColor(EnumChatFormatting.RED));
		} catch (CommandException commandexception1) {
			sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(commandexception1.getMessage(), commandexception1.getErrorOjbects()).setColor(EnumChatFormatting.RED));
		} catch (Throwable throwable) {
			sender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.generic.exception").setColor(EnumChatFormatting.RED));
			throwable.printStackTrace();
		}

        return j;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getPossibleCommands(ICommandSender sender, String par2Str) {
        String[] args = par2Str.split(" ", -1);
        String commandName = args[0];

        if (args.length == 1) {
            ArrayList<String> possibleCmds = new ArrayList<String>();

            for (Object o : super.getCommands().keySet()) {
                ICommand cmd = (ICommand) super.getCommands().get(o);

                if (cmd != null) {
                    if (CommandBase.doesStringStartWith(commandName, cmd.getCommandName())) {
        				if (forgeperms.ForgePerms.getPermissionManager().canAccess(sender.getCommandSenderName(), sender.getEntityWorld().provider.getDimensionName(), cmd.getClass().getName())){
        					possibleCmds.add(cmd.getCommandName());
        				}
                    }
                }
            }

            return possibleCmds;
        } else {
            if (args.length > 1) {
                ICommand cmd = (ICommand) super.getCommands().get(commandName);

                if (cmd != null) {

                    String[] astring1 = new String[args.length - 1];

                    System.arraycopy(args, 1, astring1, 0, args.length - 1);

                    return cmd.addTabCompletionOptions(sender, astring1);
                }
            }

            return null;
        }
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getPossibleCommands(ICommandSender sender) {
		ArrayList<ICommand> possibleCmds = new ArrayList<ICommand>();
		
		for(Object o : super.getCommands().keySet()) {
			ICommand cmd = (ICommand) super.getCommands().get(o);
			
			if (cmd != null) {
				if (forgeperms.ForgePerms.getPermissionManager().canAccess(sender.getCommandSenderName(), sender.getEntityWorld().provider.getDimensionName(), cmd.getClass().getName())){
					possibleCmds.add(cmd);
				}
			}
		}
		
		return possibleCmds;
	}
}