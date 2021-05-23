package me.sachin.lootin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sachin.lootin.commands.LootinCommand;
import me.sachin.lootin.listeners.ChestCloseEvent;
import me.sachin.lootin.listeners.ChestOpenEvent;
import me.sachin.lootin.listeners.CustomStructuresLootPopulateEvent;
import me.sachin.lootin.listeners.ItemMoveEvent;
import me.sachin.lootin.listeners.LootGenerateListener;
import me.sachin.lootin.listeners.LootinChestBreakEvent;
import me.sachin.lootin.listeners.LootinChestExplodeEvent;
import me.sachin.lootin.listeners.MinecartDeathEvent;
import me.sachin.lootin.utils.ConfigUtils;
import me.sachin.lootin.utils.LConstants;

public final class Lootin extends JavaPlugin implements Listener {

    private static ItemStack tempItem;
    private static Lootin plugin;
    private ConfigUtils config;
    private boolean papiEnabled;
    private HashMap<Player,Location> currentChestViewvers = new HashMap<>();
    private HashMap<Player,StorageMinecart> currentMinecartViewvers = new HashMap<>();
    private List<Location> currentlyEditedChest = new ArrayList<>();
    private List<NamespacedKey> keyList = new ArrayList<>();

    public List<NamespacedKey> getKeyList() {
        return keyList;
    }

    public List<Location> getCurrentlyEditedChest() {
        return currentlyEditedChest;
    }

    public HashMap<Player, Location> getCurrentChestViewvers() {
        return currentChestViewvers;
    }

    public HashMap<Player, StorageMinecart> getCurrentMinecartViewvers() {
        return currentMinecartViewvers;
    }
    
    public ConfigUtils config(){
        return config;
    }

    public static Lootin getPlugin() {
        return plugin;
    }

    public static ItemStack getTempItem() {
        return tempItem;
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.config = new ConfigUtils(this);
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ChestOpenEvent(this), this);
        pm.registerEvents(new LootGenerateListener(this), this);
        pm.registerEvents(new ChestCloseEvent(this), this);
        pm.registerEvents(new LootinChestBreakEvent(this), this);
        pm.registerEvents(new ItemMoveEvent(this), this);
        pm.registerEvents(new MinecartDeathEvent(this), this);
        pm.registerEvents(new LootinChestExplodeEvent(this),this);
        this.getCommand("lootin").setExecutor(new LootinCommand(this));

        // reloads config files
        reloadConfigFiles();

        // creates tempItem
        tempItem = new ItemStack(Material.STICK);
        ItemMeta meta = tempItem.getItemMeta();
        meta.setDisplayName("This is temporary Item");
        tempItem.setItemMeta(meta);

        // notifies if server has terra plugin but no terra addon
        if(pm.isPluginEnabled("Terra")){
            List<String> addonList = Arrays.asList(new File(pm.getPlugin("Terra").getDataFolder().getAbsolutePath()+"/addons").list());
            addonList.stream().filter(a -> {
                return a.contains("LootinAddon");
            });
            if(addonList.isEmpty()){
                getLogger().info("Terra Lootin Addon is required for loot chests in terra structures to be altered");
                getLogger().info("download addon from here https://www.spigotmc.org/resources/90868/");
            }
        }
        papiEnabled = pm.isPluginEnabled("PlaceHolderAPI");

        if(pm.isPluginEnabled("CustomStructures")){
            getLogger().info("Found CustomStructures, registering listeners");
            pm.registerEvents(new CustomStructuresLootPopulateEvent(this), this);
        }
    }

    public void reloadConfigFiles(){
        this.saveDefaultConfig();
        this.reloadConfig();
        keyList = config().getBlackListStructures();
        this.getLogger().info("Lootin plugin succesfully loaded");
    }

 

    public boolean isLootinChest(BlockState state){
        if(!(state instanceof TileState)){
            return false;
        }
        TileState tile = (TileState) state;
        PersistentDataContainer data = tile.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(this, LConstants.DATA_KEY);
        return data.has(key, PersistentDataType.STRING);
    }

    public boolean isLootinChest(StorageMinecart minecart){
        PersistentDataContainer data = minecart.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(this, LConstants.DATA_KEY);
        return data.has(key, PersistentDataType.STRING);
    }

    public boolean isLootinChestForItems(StorageMinecart minecart){
        PersistentDataContainer data = minecart.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(this, LConstants.IDENTITY_KEY);
        return data.has(key, PersistentDataType.STRING);
    }
    public boolean isLootinChestForItems(BlockState state){
        if(!(state instanceof TileState)){
            return false;
        }
        TileState tile = (TileState) state;
        PersistentDataContainer data = tile.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(this, LConstants.IDENTITY_KEY);
        return data.has(key, PersistentDataType.STRING);
    }

    public boolean hasPlayerContents(BlockState state,Player p){
        String playerId = p.getUniqueId().toString();
        TileState tile = (TileState) state;
        PersistentDataContainer data = tile.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(this, playerId);
        return data.has(key, PersistentDataType.STRING);
    }

    public boolean hasPlayerContents(StorageMinecart minecart,Player p){
        String playerId = p.getUniqueId().toString();
        PersistentDataContainer data = minecart.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(this, playerId);
        return data.has(key, PersistentDataType.STRING);
    }


    public boolean isBlackListWorld(String worldName){
        List<String> blackListWorlds = config().getBlacklistWorlds();
        if(blackListWorlds == null){
            return false;
        }
        return blackListWorlds.contains(worldName);
    }

    public boolean isBlackListChest(Chest chest){
        if(chest.getLootTable() != null){
            return getKeyList().contains(chest.getLootTable().getKey());
        }
        else{
            return false;
        }
    }

    public boolean isBlackListMinecart(StorageMinecart minecart){
        if(minecart.getLootTable() != null){
            return getKeyList().contains(minecart.getLootTable().getKey());
        }
        else{
            return false;
        }
    }

    public String setTitles(String config,Player player){
        if(papiEnabled){
            return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, getConfig().getString(config," ")));
        }
        else{
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString(config,""));
        }
    }



}
