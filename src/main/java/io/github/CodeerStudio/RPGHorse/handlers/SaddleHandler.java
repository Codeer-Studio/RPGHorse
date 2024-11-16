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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class SaddleHandler implements Listener {

    private final HashMap<UUID, Horse> playerHorses = new HashMap<>();

    public SaddleHandler(RPGHorse plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSaddleUse_Normal(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.SADDLE) {
            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();

            if (playerHorses.containsKey(playerId)) {
                Horse previousHorse = playerHorses.get(playerId);

                if (previousHorse != null && !previousHorse.isDead()) {
                    previousHorse.remove();
                    player.sendMessage("Your previous horse has been despawned");
                }
            }

            Horse newHorse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);

            // Customize the horse
            newHorse.setOwner(player);
            newHorse.setTamed(true);
            newHorse.setCustomName(player.getName() + "'s Horse");
            newHorse.setCustomNameVisible(true);
            newHorse.setAdult();
            newHorse.setMaxHealth(30.0);
            newHorse.setHealth(30.0);
            newHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE));

            // Mount the player on the new horse
            newHorse.addPassenger(player);

            // Store the new horse in the map
            playerHorses.put(playerId, newHorse);

            // Notify the player
            player.sendMessage("A new horse has been summoned for you to ride!");

            event.setCancelled(true);
        }
    }

    public void removeHorses() {
        for (Horse horse: playerHorses.values()) {
            if (horse != null && !horse.isDead()) {
                horse.remove();
            }
        }

        playerHorses.clear();
    }
}
