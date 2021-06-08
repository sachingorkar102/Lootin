package me.sachin.lootin.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.loot.LootTables;

import me.sachin.lootin.Lootin;

public class ConfigUtils {

    private Lootin plugin;

    public ConfigUtils(Lootin plugin){
        this.plugin = plugin;

    }

    public List<String> getBlacklistWorlds(){
        List<String> list = new ArrayList<>();
        try {
            list = plugin.getConfig().getStringList(LConstants.BLACK_LIST_CONFIG);
            if(list.isEmpty()) return null;
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public String getBlockBreakMessage(){
        String message = plugin.getConfig().getString(LConstants.BLOCK_BREAK_MESSAGE," ");
        return ChatColor.translateAlternateColorCodes('&', 
        message);
    }

    public boolean deleteItemsOnBreak(){
        return plugin.getConfig().getBoolean(LConstants.DELETE_ITEMS_CONFIG,true);
    }
    

    public boolean changeMinecarts(){
        return plugin.getConfig().getBoolean(LConstants.CHANGE_MINECARTS_CONFIG,true);
    }

    public String getTitle(String key){
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(key,"Chest"));
    }

    public String getBlockBreakWithoutPermMessage(){
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(LConstants.BLOCK_BREAK_WITHOUT_PERM_MESSAGE," "));
    }


    public List<NamespacedKey> getBlackListStructures(){
        List<String> list = new ArrayList<>();
        List<NamespacedKey> keyList = new ArrayList<>();
        try {
            list = plugin.getConfig().getStringList(LConstants.BLACKLIST_STRUCTURES);
            if(list.isEmpty() || list == null) return keyList;
            list.forEach(s -> {
                if(LootTables.valueOf(s) != null){
                    keyList.add(LootTables.valueOf(s).getKey());
                }
            });
            return keyList;
        } catch (Exception e) {
            return keyList;
        }
    }

    public boolean preventExplosion(){
        return plugin.getConfig().getBoolean(LConstants.PREVENT_EXPLOSION,true);
    }


    public List<String> getBlackListTerraStructures(){
        return plugin.getConfig().getStringList(LConstants.BLACKLIST_TERRA_STRUCTURES);
    }

    public List<String> getBlackListCustomStructures(){
        return plugin.getConfig().getStringList(LConstants.BLACKLIST_CUSTOM_STRUCTURES);
    }

    public List<String> getBlackListOTDStructures(){
        return plugin.getConfig().getStringList(LConstants.BLACKLIST_OTD_STRUCTURES) == null ? new ArrayList<>() : plugin.getConfig().getStringList(LConstants.BLACKLIST_OTD_STRUCTURES);
    }
}
