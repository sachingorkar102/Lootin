package me.sachin.lootin.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.ItemSerializer;
import me.sachin.lootin.utils.LConstants;

public class MinecartDeathEvent implements Listener{

    private Lootin plugin;


    public MinecartDeathEvent(Lootin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMinecartDeath(VehicleDestroyEvent e){
        if(plugin.isBlackListWorld(e.getVehicle().getWorld().getName())) return;
        if(!(e.getVehicle() instanceof StorageMinecart)) return;

        StorageMinecart minecart = (StorageMinecart) e.getVehicle();
        if(plugin.isBlackListMinecart(minecart)) return;
        if(minecart.getLootTable() != null || plugin.isLootinChestForItems(minecart) || plugin.isLootinChest(minecart)){
            if(!(e.getAttacker() instanceof Player)){
                if(plugin.config().preventExplosion()){
                    e.setCancelled(true);
                }
                if(plugin.config().deleteItemsOnBreak()){
                    minecart.getInventory().clear();
                }
                return;
            }
            Player p = (Player) e.getAttacker();
            World world = p.getWorld();
            if(!p.hasPermission(LConstants.BREAK_CHEST_PERM)){
                e.setCancelled(true);
                String blockBreakMessage = plugin.config().getBlockBreakWithoutPermMessage();
                if(blockBreakMessage != null){
                    p.sendMessage(blockBreakMessage);
                }
                return;
            }


            if(p.isSneaking()){
                
                // if delete items on break option is true then clear minecart inventory and return
                if(plugin.config().deleteItemsOnBreak()){
                    minecart.getInventory().clear();
                    return;    
                }
                // if its not lootin chest but is a loottable chest then return
                if(!plugin.isLootinChest(minecart)){
                    return;
                }
                
                minecart.getInventory().clear();
                List<ItemStack> dropItems = new ArrayList<>();
                if(plugin.hasPlayerContents(minecart, p)){
                    dropItems = ItemSerializer.getItems(minecart, p.getUniqueId().toString());
                }
                else{
                    dropItems = ItemSerializer.getItems(minecart, LConstants.DATA_KEY);
                }
                if(dropItems.isEmpty()) return;
                for (ItemStack itemStack : dropItems) {
                    if(itemStack.getType().name().equals("AIR")){
                        continue;
                    }
                    world.dropItemNaturally(minecart.getLocation(), itemStack);
                }
            }
            else{
                e.setCancelled(true);
                String message = plugin.config().getBlockBreakMessage();
                
                if(message != "" && message != null){
                    p.sendMessage(message);
                }
            }
        }            
    }
    
}
