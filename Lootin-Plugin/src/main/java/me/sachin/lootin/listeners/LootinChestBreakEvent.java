package me.sachin.lootin.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.ItemSerializer;
import me.sachin.lootin.utils.LConstants;
import net.md_5.bungee.api.ChatColor;

public class LootinChestBreakEvent implements Listener{

    private Lootin plugin;

    public LootinChestBreakEvent(Lootin plugin){
        this.plugin = plugin;
    }

    private boolean hasLootTable(BlockState state){
        if(!(state instanceof Chest)){
            return false;
        }
        Chest chest = (Chest) state;
        return chest.getLootTable() != null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChestBreak(BlockBreakEvent e){
        World world = e.getBlock().getWorld();
        BlockState state = e.getBlock().getState();
        if(plugin.isBlackListWorld(world.getName())){
            return;
        }

        // check if the block is chest or not
        if((state instanceof Container)){
            Container chest = (Container) state;
            Player p = e.getPlayer();
            // checks if another player is viewing chest or not
            if(plugin.getCurrentlyEditedChest().contains(chest.getLocation())){
                e.setCancelled(true);
                String chestEditMessage = plugin.setTitles(LConstants.CHEST_EDITING, p);
                if(chestEditMessage != null){
                    p.sendMessage(chestEditMessage);
                }
                return;
            }
            if(plugin.isBlackListChest(chest)) return;

            // check if the chest has a lootTable or it is a lootin tagged chest
            if(hasLootTable(state) || plugin.isLootinChest(state) || plugin.isLootinChestForItems(state)){
                if(!p.hasPermission(LConstants.BREAK_CHEST_PERM)){
                    e.setCancelled(true);
                    String blockBreakMessage = plugin.setTitles(LConstants.BLOCK_BREAK_WITHOUT_PERM_MESSAGE, p);
                    if(blockBreakMessage != null){
                        p.sendMessage(blockBreakMessage);
                    }
                    return;
                }
      
                if(p.isSneaking() ){
                    
                    // if delete items on break option is true then clear chest inventory and return
                    if(plugin.config().deleteItemsOnBreak()){
                        chest.getInventory().clear();
                        return;    
                    }
                    // if its not lootin chest but is a loottable chest then return
                    if(!plugin.isLootinChest(state)){
                        return;
                    }
                    
                    chest.getInventory().clear();
                    List<ItemStack> dropItems = new ArrayList<>();
                    if(plugin.hasPlayerContents(state, p)){
                        dropItems = ItemSerializer.getItems((TileState)state, p.getUniqueId().toString());
                    }
                    else{
                        dropItems = ItemSerializer.getItems((TileState)state, LConstants.DATA_KEY);
                    }
                    if(dropItems.isEmpty()) return;
                    for (ItemStack itemStack : dropItems) {
                        if(itemStack.getType().name().equals("AIR")){
                            continue;
                        }
                        world.dropItemNaturally(e.getBlock().getLocation(), itemStack);
                    }
                }
                else{
                    e.setCancelled(true);
                    String message = plugin.setTitles(LConstants.BLOCK_BREAK_MESSAGE, p);
                    
                    if(message != "" && message != null){
                        p.sendMessage(message);
                    }
                }
            }
            
        }

    }

    

    
}
