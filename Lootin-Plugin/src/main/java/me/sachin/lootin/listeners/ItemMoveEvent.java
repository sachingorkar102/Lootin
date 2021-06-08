package me.sachin.lootin.listeners;

import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import me.sachin.lootin.Lootin;


public class ItemMoveEvent implements Listener{

    private Lootin plugin;

    public ItemMoveEvent(Lootin plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onItemTransport(InventoryMoveItemEvent e){
        String worldName = e.getSource().getLocation().getWorld().getName();
        if(plugin.isBlackListWorld(worldName)){
            return;
        }
        BlockState initialBlock = e.getSource().getLocation().getBlock().getState();
        BlockState finalBlock = e.getDestination().getLocation().getBlock().getState();
        if((initialBlock instanceof Chest)){
            Chest initialChest = (Chest) initialBlock;
            if(initialChest.getLootTable() != null || plugin.isLootinChestForItems(initialBlock)){
              
                e.setCancelled(true);
            }
            
        }
        else if((finalBlock instanceof Chest)){
            Chest finalChest = (Chest) finalBlock;
            if(finalChest.getLootTable() != null || plugin.isLootinChestForItems(finalBlock)){
                e.setCancelled(true);
            }
        }

        if((initialBlock instanceof Barrel)){
            Barrel initialChest = (Barrel) initialBlock;
            if(initialChest.getLootTable() != null || plugin.isLootinChestForItems(initialBlock)){
              
                e.setCancelled(true);
            }
            
        }
        else if((finalBlock instanceof Barrel)){
            Barrel finalChest = (Barrel) finalBlock;
            if(finalChest.getLootTable() != null || plugin.isLootinChestForItems(finalBlock)){
                e.setCancelled(true);
            }
        }
    }
    
}
