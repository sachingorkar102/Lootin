package me.sachin.lootinterraaddon;

import com.dfsek.terra.api.TerraPlugin;
import com.dfsek.terra.api.addons.TerraAddon;
import com.dfsek.terra.api.addons.annotations.Addon;
import com.dfsek.terra.api.addons.annotations.Author;
import com.dfsek.terra.api.addons.annotations.Depends;
import com.dfsek.terra.api.addons.annotations.Version;
import com.dfsek.terra.api.event.EventListener;
import com.dfsek.terra.api.event.annotations.Global;
import com.dfsek.terra.api.event.events.config.ConfigPackPreLoadEvent;
import com.dfsek.terra.api.event.events.world.generation.LootPopulateEvent;
import com.dfsek.terra.api.injection.annotations.Inject;
import com.dfsek.terra.bukkit.world.BukkitAdapter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.LConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
@Addon("LootinAddon") 
@Author("sachingorkar")
@Version("0.2") 
@Depends({})
public class LootinAddon extends TerraAddon implements EventListener {
    @Inject 
    private TerraPlugin main;

    @Inject 
    private Logger logger;

    private boolean isLootinLoaded;
    private List<String> blackListStructures = new ArrayList<>();

    
    public void onPackLoad(ConfigPackPreLoadEvent event) { 
        logger.info("Lootin addon successfully loaded");
    }


    public void onLootPopulate(LootPopulateEvent event) {
        if(!Bukkit.getPluginManager().isPluginEnabled("Lootin")) {
            return;
        }
        
        Location location = BukkitAdapter.adapt(event.getBlock().getLocation());
        BlockState state = location.getBlock().getState();
        if(state instanceof Chest){
            blackListStructures = Lootin.getPlugin().config().getBlackListTerraStructures();
            if(!blackListStructures.isEmpty() || blackListStructures != null){
                if(blackListStructures.contains(event.getStructureScript().getId())) return;
            }
            TileState tile = (TileState) state;
            PersistentDataContainer data = tile.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Lootin.getPlugin(), LConstants.IDENTITY_KEY);
            data.set(key, PersistentDataType.STRING, "");
            tile.update();
        }

    }

    @Override
    public void initialize() {
        main.getEventManager().registerListener(this, this);
    }
}