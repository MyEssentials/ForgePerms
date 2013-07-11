package com.sperion.forgeperms;

import net.minecraft.command.ICommandSender;

public abstract class PermissionsBase {
    public boolean loaded = false;
    public String name = "Unknown";
    public String loadError = "Unknown";

    /**
     * Loads the PermissionHandler, typically used to check if the Permission
     * Manager is there and register anything with the manager
     * 
     * @return
     */
    public abstract boolean load();

    /**
     * Checks if a user has the permission node in the given world
     * 
     * @param name
     * @param world
     * @param node
     * @return
     */
    public abstract boolean canAccess(String name, String world, String node);

    /**
     * Gets the prefix of the player in the given world
     * 
     * @param player
     * @param world
     * @return
     */
    public abstract String getPrefix(String player, String world);

    /**
     * Gets the suffix/postfix of the player in the given world
     * 
     * @param player
     * @param world
     * @return
     */
    public abstract String getPostfix(String player, String world);

    /**
     * Gets the option of the player in the given world.
     * 
     * @param player
     * @param world
     * @param node
     * @param def
     * @return
     */
    public abstract String getOption(String player, String world, String node,
            String def);

    /**
     * Gets the option of the player in the given world.
     * 
     * @param name
     * @param node
     * @param def
     * @return
     */
    public abstract String getOption(ICommandSender name, String node,
            String def);
}