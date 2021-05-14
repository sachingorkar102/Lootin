package me.sachin.lootin.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.sachin.lootin.Lootin;

public class LootinChestExplodeEvent implements Listener{

    private Lootin plugin;

    public LootinChestExplodeEvent(Lootin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onChestExplode(EntityExplodeEvent e){
        if(plugin.isBlackListWorld(e.getEntity().getWorld().getName()) || !plugin.config().preventExplosion()) return;
        for (Block block : e.blockList().toArray(new Block[0])){
            BlockState state = block.getState();
            if((state instanceof Chest) || (state instanceof DoubleChest)){
                Chest chest = (Chest) state;
                if(plugin.isBlackListChest(chest)) return;
                if(chest.getLootTable() != null || plugin.isLootinChest(state) || plugin.isLootinChestForItems(state)){
                    e.blockList().remove(block);
                }
            }
        }
    }
    
}
