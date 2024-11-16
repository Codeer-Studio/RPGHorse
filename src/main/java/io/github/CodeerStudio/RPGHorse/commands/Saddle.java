package io.github.CodeerStudio.RPGHorse.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Saddle implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack saddle = new ItemStack(Material.SADDLE);

        player.getInventory().addItem(saddle);

        player.sendMessage("You have been given a saddle");
        return true;
    }
}
