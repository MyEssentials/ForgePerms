package com.sperion.forgeperms.api;

import net.minecraft.command.ICommandSender;

/**
 * Base for all permission handlers
 * @author Joe Goett
 */
public interface IPermissionManager {
    /**
     * Returns the name of the permission manager
     * @return
     */
    public String getName();
    
    /**
     * Loads the PermissionHandler, typically used to check if the Permission
     * Manager is there and register anything with the manager
     * 
     * @return
     */
    public boolean load();
    
    /**
     * Gets the load error string
     * @return
     */
    public String getLoadError();
    
    /**
     * Checks if a user has the permission node in the given world
     * 
     * @param name
     * @param world
     * @param node
     * @return
     */
    public boolean canAccess(String name, String world, String node);

    /**
     * Gets the option of the player in the given world.
     * 
     * @param player
     * @param world
     * @param node
     * @param def
     * @return
     */
    public String getOption(String player, String world, String node, String def);

    /**
     * Gets the option of the player in the given world.
     * 
     * @param name
     * @param node
     * @param def
     * @return
     */
    public String getOption(ICommandSender name, String node, String def);
}