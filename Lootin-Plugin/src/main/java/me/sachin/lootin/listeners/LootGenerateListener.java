package me.sachin.lootin.listeners;


import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.LConstants;
import net.md_5.bungee.api.chat.hover.content.Item;

public class LootGenerateListener implements Listener{

    private Lootin plugin;

    public LootGenerateListener(Lootin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent e){
        BlockState state = e.getLootContext().getLocation().getBlock().getState();
        if(plugin.isBlackListWorld(e.getLootContext().getLocation().getWorld().getName())) return;
        NamespacedKey key = new NamespacedKey(plugin, LConstants.IDENTITY_KEY);
        if(plugin.getKeyList().contains(e.getLootTable().getKey())) return;
        if((e.getInventoryHolder() instanceof StorageMinecart) && plugin.config().changeMinecarts()){
            
            StorageMinecart minecart = (StorageMinecart) e.getInventoryHolder();
            PersistentDataContainer data = minecart.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING, "");
            
       
        }
        else if(e.getInventoryHolder() instanceof Chest){
            
            TileState tile = (TileState) state;
            PersistentDataContainer data = tile.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING, "");
            tile.update();
        }
    }


    

}
