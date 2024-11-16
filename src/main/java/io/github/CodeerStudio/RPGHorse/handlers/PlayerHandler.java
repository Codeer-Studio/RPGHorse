package io.github.CodeerStudio.RPGHorse.handlers;

import io.github.CodeerStudio.RPGHorse.RPGHorse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerHandler implements Listener {

    public PlayerHandler(RPGHorse plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
