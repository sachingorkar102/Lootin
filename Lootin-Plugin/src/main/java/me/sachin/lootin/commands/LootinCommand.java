package me.sachin.lootin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sachin.lootin.Lootin;
import me.sachin.lootin.utils.LConstants;

public class LootinCommand implements CommandExecutor{
    private Lootin plugin;

    public LootinCommand(Lootin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if((sender instanceof Player)){
            Player p = (Player) sender;
            if(!p.hasPermission(LConstants.LOOTIN_RELOAD_COMMAND_PERM)){
                return true;
            }
            if(args.length < 1){
                p.sendMessage(ChatColor.RED+"Invalid arguments");
                return true;
            }
            if(args[0].equals("reload")){
                plugin.reloadConfigFiles();
                p.sendMessage(ChatColor.GREEN+"Lootin config file reloaded successfully");
            }
            return true;

        }
        else{
            if(args.length < 1){
                Bukkit.getLogger().info("Invalid args");
                return true;
            }
            if(args[0].equals("reload")){
                plugin.reloadConfigFiles();
            }
            return true;
        }
    }
    
}
