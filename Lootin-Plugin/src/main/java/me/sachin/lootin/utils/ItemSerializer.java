package me.sachin.lootin.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import me.sachin.lootin.Lootin;

public class ItemSerializer {

    



    /**
     * 
     * @param items
     * @param tile
     * @param keyName
     */
    public static void storeItems(List<ItemStack> items, TileState tile,String keyName){

        PersistentDataContainer data = tile.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Lootin.getPlugin(), keyName);
        ItemStack tempItem = Lootin.getTempItem();

        if (items.size() == 0){
            data.set(key, PersistentDataType.STRING, "");
            tile.update();
        }else{

            try{

                ByteArrayOutputStream io = new ByteArrayOutputStream();
                BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

                os.writeInt(items.size());
                for (ItemStack i : items) {
                    if(i == null){
                        i = tempItem;
                    }
                    os.writeObject(i);
                }

                os.flush();

                byte[] rawData = io.toByteArray();

                String encodedData = Base64.getEncoder().encodeToString(rawData);
             

                data.set(key, PersistentDataType.STRING, encodedData);

                tile.update();
                os.close();

            }catch (IOException ex){
                System.out.println(ex);
            }

        }

    }

    public static ArrayList<ItemStack> getItems(TileState tile,String keyName){

        PersistentDataContainer data = tile.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Lootin.getPlugin(), keyName);
        ItemStack tempItem = Lootin.getTempItem();

        ArrayList<ItemStack> items = new ArrayList<>();
        ArrayList<ItemStack> pitems = new ArrayList<>();

        String encodedItems = data.get(key, PersistentDataType.STRING);

        byte[] rawData = Base64.getDecoder().decode(encodedItems);

        try{

            ByteArrayInputStream io = new ByteArrayInputStream(rawData);
            BukkitObjectInputStream in = new BukkitObjectInputStream(io);

            int itemsCount = in.readInt();
            for (int i = 0; i < itemsCount; i++){
                items.add((ItemStack) in.readObject());
            }
            ItemStack newi = new ItemStack(Material.AIR);
            items.forEach(i -> {
                if(i.isSimilar(tempItem)){
                    pitems.add(newi);
                }
                else{
                    pitems.add(i);
                }
            });
            in.close();

        }catch (Exception ex){
            System.out.println(ex);
        }
        return pitems;
    }




    public static void storeItems(List<ItemStack> items, StorageMinecart minecart,String keyName){

        PersistentDataContainer data = minecart.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Lootin.getPlugin(), keyName);
        ItemStack tempItem = Lootin.getTempItem();

        if (items.size() == 0){
            data.set(key, PersistentDataType.STRING, "");
     
        }else{

            try{

                ByteArrayOutputStream io = new ByteArrayOutputStream();
                BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

                os.writeInt(items.size());
                for (ItemStack i : items) {
                    if(i == null){
                        i = tempItem;
                    }
                    os.writeObject(i);
                }

                os.flush();

                byte[] rawData = io.toByteArray();

                String encodedData = Base64.getEncoder().encodeToString(rawData);
              

                data.set(key, PersistentDataType.STRING, encodedData);

            
                os.close();

            }catch (IOException ex){
                System.out.println(ex);
            }

        }

    }

    public static ArrayList<ItemStack> getItems(StorageMinecart minecart,String keyName){

        PersistentDataContainer data = minecart.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Lootin.getPlugin(), keyName);
        ItemStack tempItem = Lootin.getTempItem();

        ArrayList<ItemStack> items = new ArrayList<>();
        ArrayList<ItemStack> pitems = new ArrayList<>();

        String encodedItems = data.get(key, PersistentDataType.STRING);
   
        byte[] rawData = Base64.getDecoder().decode(encodedItems);

        try{

            ByteArrayInputStream io = new ByteArrayInputStream(rawData);
            BukkitObjectInputStream in = new BukkitObjectInputStream(io);

            int itemsCount = in.readInt();
            for (int i = 0; i < itemsCount; i++){
                items.add((ItemStack) in.readObject());
            }
            ItemStack newi = new ItemStack(Material.AIR);
            items.forEach(i -> {
                if(i.isSimilar(tempItem)){
                    pitems.add(newi);
                }
                else{
                    pitems.add(i);
                }
            });
            in.close();

        }catch (Exception ex){
            System.out.println(ex);
        }
        return pitems;
    }

}