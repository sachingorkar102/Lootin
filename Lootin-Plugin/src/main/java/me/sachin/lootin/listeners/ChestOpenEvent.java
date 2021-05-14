package me.sachin.lootin.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.ItemSerializer;
import me.sachin.lootin.utils.LConstants;

public class ChestOpenEvent implements Listener{

    private Lootin plugin;

    public ChestOpenEvent(Lootin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e){
        String worldName = e.getPlayer().getWorld().getName();
        if(plugin.isBlackListWorld(worldName)) return;
        Player p = (Player) e.getPlayer();
        if((e.getInventory().getHolder() instanceof Chest) || (e.getInventory().getHolder() instanceof DoubleChest)){
            BlockState state = e.getInventory().getLocation().getBlock().getState();
            Chest chest = (Chest) state;
            if(plugin.isBlackListChest(chest)) return;
            if(chest.getLootTable() != null || plugin.isLootinChestForItems(state)){
                if((chest.getInventory() instanceof DoubleChestInventory)){
                    DoubleChest dChest = (DoubleChest) chest.getInventory().getHolder();
                    Chest leftSide = (Chest) dChest.getLeftSide();
                    Chest rightSide = (Chest) dChest.getRightSide();
                    List<ItemStack> leftSideItems = setChestInventory(p, leftSide);
                    List<ItemStack> rightSideItems = setChestInventory(p, rightSide);
                    List<ItemStack> mainItems = new ArrayList<>();
                    Stream.of(leftSideItems,rightSideItems).forEach(mainItems::addAll);
                    Inventory inv = Bukkit.createInventory(p, 54,plugin.config().getTitle(LConstants.DOUBLE_CHEST_TITLE));
                    inv.setContents(mainItems.toArray(new ItemStack[0]));
                    plugin.getCurrentlyEditedChest().add(leftSide.getLocation());
                    plugin.getCurrentlyEditedChest().add(rightSide.getLocation());
                    
                    e.setCancelled(true);
                    chest.open();
                    
                    p.openInventory(inv);
                }
                else{
                    List<ItemStack> items = setChestInventory(p, chest);
                    Inventory inv = Bukkit.createInventory(p, 27, plugin.config().getTitle(LConstants.SINGLE_CHEST_TITLE));
                    e.setCancelled(true);
                    chest.open();
                    inv.setContents(items.toArray(new ItemStack[0]));
                    
                    p.openInventory(inv);
                    plugin.getCurrentlyEditedChest().add(chest.getLocation());              
                }
                plugin.getCurrentChestViewvers().put(p, state.getBlock().getLocation());
                
                // new BukkitRunnable(){
                //     @Override
                //     public void run() {   
                //     }
                            
                // }.runTaskLater(plugin, 1);
            }
            
            
        }
        else if ((e.getInventory().getHolder() instanceof StorageMinecart) && plugin.config().changeMinecarts()){
            StorageMinecart minecart = (StorageMinecart) e.getInventory().getHolder();
            if(plugin.isBlackListMinecart(minecart)) return;
            if(minecart.getLootTable() != null || plugin.isLootinChestForItems(minecart)){
                List<ItemStack> items = setChestInventory(p, minecart);
                Inventory inv = Bukkit.createInventory(p, 27,plugin.config().getTitle(LConstants.MINECART_TITLE));
                inv.setContents(items.toArray(new ItemStack[0]));
                e.setCancelled(true);
                p.openInventory(inv);
                plugin.getCurrentlyEditedChest().add(minecart.getLocation());
                plugin.getCurrentMinecartViewvers().put(p, minecart); 
                // new BukkitRunnable(){
                //     @Override
                //     public void run() {
                        
                //     }
                // }.runTaskLater(plugin, 2);
            }    
                

        }
        
    }


    public List<ItemStack> setChestInventory(Player p,StorageMinecart minecart){
        List<ItemStack> items = new ArrayList<>();
        if(plugin.hasPlayerContents(minecart, p)){
            items = ItemSerializer.getItems(minecart, p.getUniqueId().toString());
        }
        else if(plugin.isLootinChest(minecart)){
            items = ItemSerializer.getItems(minecart, LConstants.DATA_KEY);
        }
        else{
            items = Arrays.asList(minecart.getInventory().getContents());
            ItemSerializer.storeItems(items, minecart, LConstants.DATA_KEY);
            minecart.getInventory().clear();
        }
        return items;
    }

    public List<ItemStack> setChestInventory(Player p,Chest chest){
        List<ItemStack> items = new ArrayList<>();
        BlockState state = chest.getBlock().getState();
        if(plugin.hasPlayerContents(state, p)){
            items = ItemSerializer.getItems((TileState)state, p.getUniqueId().toString());
        }
        else if(plugin.isLootinChest(state)){
            items = ItemSerializer.getItems((TileState)state, LConstants.DATA_KEY);
        }
        else{
            items = Arrays.asList(chest.getBlockInventory().getContents());
            ItemSerializer.storeItems(items, (TileState)state, LConstants.DATA_KEY);
            chest.getBlockInventory().clear();
        }
        return items;


    }


    
}
