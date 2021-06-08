package me.sachin.lootin.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.LConstants;
import otd.api.event.ChestEvent;

public class OTDListener implements Listener{


    private Lootin plugin;

    public OTDListener(Lootin plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onLootGenerate(ChestEvent e){
        BlockState state = e.getLocation().getBlock().getState();
        if(plugin.config().getBlackListOTDStructures().contains(e.getType().name())) return;
        if(!(state instanceof Barrel) || !(state instanceof Chest)) return;
        NamespacedKey key = new NamespacedKey(plugin, LConstants.IDENTITY_KEY);
        if(state instanceof Container){
            Container container = (Container) state;
            PersistentDataContainer data = container.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING, "");
            container.update();
        }
        
    }
}