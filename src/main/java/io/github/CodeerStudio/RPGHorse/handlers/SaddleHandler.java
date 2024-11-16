package io.github.CodeerStudio.RPGHorse.handlers;

import io.github.CodeerStudio.RPGHorse.RPGHorse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SaddleHandler implements Listener {

    public SaddleHandler(RPGHorse plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSaddleUse_Normal(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            ItemStack item = event.getItem();

            if (item != null && item.getType() == Material.SADDLE) {
                Player player = event.getPlayer();

                Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);

                horse.setOwner(player);
                horse.setTamed(true);
                horse.setCustomName(player.getName() + "'s Horse");
                horse.setCustomNameVisible(true);
                horse.setAdult();
                horse.setMaxHealth(30.0); // Set the horse's health
                horse.setHealth(30.0);
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));


                horse.addPassenger(player);

                player.sendMessage("A horse has been summoned for you to ride!");
                event.setCancelled(true);
            }
        }
    }


}
