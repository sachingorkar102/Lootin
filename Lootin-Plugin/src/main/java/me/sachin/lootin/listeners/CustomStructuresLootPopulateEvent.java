package me.sachin.lootin.listeners;

import com.ryandw11.structure.api.LootPopulateEvent;

import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.LConstants;

public class CustomStructuresLootPopulateEvent implements Listener{

    private Lootin plugin;

    public CustomStructuresLootPopulateEvent(Lootin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void lootPopulateEvent(LootPopulateEvent e){
        if(plugin.config().getBlackListCustomStructures().contains(e.getStructure().getName())) return;
        if(plugin.isBlackListWorld(e.getLocation().getWorld().getName())) return;
        BlockState state = e.getLocation().getBlock().getState();
        Container container = (Container) state;
        Inventory cInventory = container.getInventory();
        NamespacedKey key = new NamespacedKey(plugin, LConstants.IDENTITY_KEY);
        if(cInventory.getHolder() instanceof Chest){
            PersistentDataContainer data = container.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING, "");
            container.update();
        }
    }
    
}
