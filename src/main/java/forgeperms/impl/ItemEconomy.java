package forgeperms.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ForgeEventFactory;
import forgeperms.api.IEconomyManager;

public class ItemEconomy implements IEconomyManager {
	@Override
	public boolean load() {
		return true; // Will always successfully load
	}

	@Override
	public String getLoadError() {
		return null; // No need for an error since it will always successfully
						// load
	}

	@Override
	public String getName() {
		return "ItemEcon";
	}

	@Override
	public double playerBalance(String playerName, String itemID, String world) {
		String[] itemInfo = itemID.split(":");
		EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
		for (ItemStack item : player.inventory.mainInventory) {
			if (item.itemID == Integer.parseInt(itemInfo[0]) && item.getItemDamage() == Integer.parseInt(itemInfo[1])) {
				return item.stackSize;
			}
		}

		return 0;
	}

	@Override
	public boolean playerHas(String playerName, String world, String itemID, double amount) {
		return true; // Pretend the player always has enough, since it is item
						// based
	}

	@Override
	public boolean playerWithdraw(String playerName, String world, String itemID, double amount) {
		String[] itemInfo = itemID.split(":");
		EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
		ItemStack held = player.getHeldItem();
		ItemStack item = new ItemStack(Integer.parseInt(itemInfo[0]), (int) amount, Integer.parseInt(itemInfo[1]));

		if (held == null || held.itemID != item.itemID || held.getItemDamage() != item.getItemDamage()) { // Makes sure the player has the correct item in their hand
			return false;
		}

		if (held.stackSize < item.stackSize) {
			return false;
		}

		held.stackSize -= item.stackSize; // Removes specific amount of items from players inventory
		if (held.stackSize <= 0) {
			ForgeEventFactory.onPlayerDestroyItem(player, held); // Removes the item completely from the players inventory
		}
		return true; // Withdraw was successful!
	}

	@Override
	public boolean playerDeposit(String playerName, String world, String itemID, double amount) {
		String[] itemInfo = itemID.split(":");
		EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(playerName);
		ItemStack item = new ItemStack(Integer.parseInt(itemInfo[0]), (int) amount, Integer.parseInt(itemInfo[1]));

		if (!player.inventory.addItemStackToInventory(item)) {
			return false; // Players inventory was full, deposit failed!
		}

		return true; // Deposit was successful!
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public String format(String itemID, double amount) {
		String[] itemInfo = itemID.split(":");
		ItemStack item = new ItemStack(Integer.parseInt(itemInfo[0]), (int) amount, Integer.parseInt(itemInfo[1]));
		return Double.toString(amount) + " " + item.getDisplayName();
	}

	@Override
	public boolean rightClickToPay() {
		return true;
	}

	@Override
	public double bankBalance(String name, String itemID) {
		return 0;
	}

	@Override
	public boolean bankHas(String name, String itemID, double amount) {
		return false;
	}

	@Override
	public boolean bankWithdraw(String name, String itemID, double amount) {
		return false;
	}

	@Override
	public boolean bankDeposit(String name, String itemID, double amount) {
		return false;
	}
}