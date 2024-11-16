package io.github.CodeerStudio.RPGHorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Saddle implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack saddle = new ItemStack(Material.SADDLE);

        ItemMeta meta = saddle.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("Basic Horse");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Speed: " + ChatColor.YELLOW + "0.01");
            lore.add(ChatColor.GREEN + "Jump Power: " + ChatColor.YELLOW + "1.0");
            lore.add(ChatColor.GRAY + "Use it wisely, and your horse will grow stronger!");
            meta.setLore(lore);

            saddle.setItemMeta(meta);
        }

        player.getInventory().addItem(saddle);

        player.sendMessage("You have been given a saddle");
        return true;
    }
}
