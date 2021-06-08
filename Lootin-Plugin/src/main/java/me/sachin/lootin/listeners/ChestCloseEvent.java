package me.sachin.lootin.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.ItemSerializer;

public class ChestCloseEvent implements Listener{

    private Lootin plugin;

    public ChestCloseEvent(Lootin plugin){
        this.plugin = plugin;

    }

    @EventHandler
    public void onChestClose(InventoryCloseEvent e){
        String worldName = e.getPlayer().getWorld().getName();
        if(plugin.isBlackListWorld(worldName)){
            return;
        }
        Player p = (Player) e.getPlayer();
        Inventory inventory = e.getInventory();
        String playerId = p.getUniqueId().toString();
        if(plugin.getCurrentChestViewvers().keySet().contains(p)){
            List<ItemStack> contents = Arrays.asList(inventory.getContents());
            List<Location> locList = plugin.getCurrentlyEditedChest();
            BlockState state = plugin.getCurrentChestViewvers().get(p).getBlock().getState();
            if(state instanceof Barrel){
                Barrel barrel = (Barrel) state;
                ItemSerializer.storeItems(contents, (TileState)state, playerId);
                barrel.close();
                locList.remove(barrel.getLocation());
                return;
            }
            Chest chest = (Chest) state;
            if((chest.getInventory() instanceof DoubleChestInventory) && inventory.getSize() == 54){
                DoubleChest dChest = (DoubleChest) chest.getInventory().getHolder();
                Chest leftside = (Chest) dChest.getLeftSide();
                Chest rightside = (Chest) dChest.getRightSide();
                locList.remove(leftside.getLocation());
                locList.remove(rightside.getLocation());
                chest.close();
                ItemSerializer.storeItems(contents.subList(0, 27), (TileState)leftside.getBlock().getState(), playerId);
                ItemSerializer.storeItems(contents.subList(27, 54), (TileState)rightside.getBlock().getState(), playerId);
            }
            else{
                ItemSerializer.storeItems(contents, (TileState)state, playerId);
                locList.remove(chest.getLocation());
                chest.close();
            }
            plugin.getCurrentChestViewvers().remove(p);
        }
        else if(plugin.getCurrentMinecartViewvers().keySet().contains(p)){
            List<ItemStack> contents = Arrays.asList(inventory.getContents());
            List<Location> locList = plugin.getCurrentlyEditedChest();
            StorageMinecart minecart = plugin.getCurrentMinecartViewvers().get(p);
            ItemSerializer.storeItems(contents, minecart, playerId);
            locList.remove(minecart.getLocation());
            plugin.getCurrentMinecartViewvers().remove(p);
        }
    }
    
    public void storeItems(Chest chest,List<ItemStack> items){

    }

}
