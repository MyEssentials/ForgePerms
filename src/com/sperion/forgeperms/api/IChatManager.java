package com.sperion.forgeperms.api;

public interface IChatManager {
    /**
     * Returns the name of the chat handler in use. Used in console output and debugging.
     * @return
     */
    public String getName();
    
    /**
     * Loads the economy handler
     * @return
     */
    public boolean load();
    
    /**
     * Gets the load error string
     * @return
     */
    public String getLoadError();
    
    /**
     * Gets the prefix of the player in the given world
     * @param world
     * @param player
     * @return
     */
    public String getPlayerPrefix(String world, String player);
    
    /**
     * Gets the suffix of the player in the given world
     * @param world
     * @param player
     * @return
     */
    public String getPlayerSuffix(String world, String player);
    
    /**
     * Sets the prefix of the player in the given world
     * @param world
     * @param player
     * @param prefix
     */
    public void setPlayerPrefix(String world, String player, String prefix);
    
    /**
     * Sets the suffix of the player in the given world
     * @param world
     * @param player
     * @param suffix
     */
    public void setPlayerSuffix(String world, String player, String suffix);
    
    /**
     * Gets the prefix of the group in the given world
     * @param world
     * @param group
     * @return
     */
    public String getGroupPrefix(String world, String group);
    
    /**
     * Gets the suffix of the group in the given world
     * @param world
     * @param group
     * @return
     */
    public String getGroupSuffix(String world, String group);
    
    /**
     * Sets the prefix of the group in the given world
     * @param world
     * @param group
     * @param prefix
     */
    public void setGroupPrefix(String world, String group, String prefix);
    
    /**
     * Sets the suffix of the group in the given world
     * @param world
     * @param group
     * @param suffix
     */
    public void setGroupSuffix(String world, String group, String suffix);
    
    /**
     * Sees if the player is in the group in the given world
     * @param world
     * @param player
     * @param group
     * @return
     */
    public boolean playerInGroup(String world, String player, String group);
    
    /**
     * Gets the groups the player is in in the given world
     * @param world
     * @param player
     * @return
     */
    public String[] getPlayerGroups(String world, String player);
    
    /**
     * Gets the users primary group in the given world
     * @param world
     * @param player
     * @return
     */
    public String getPrimaryGroup(String world, String player);
    

    public String getOption(String player, String world, String node, String def);
}