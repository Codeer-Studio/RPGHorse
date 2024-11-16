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
    private final int HORSE_RANGE = 25;

    /**
     * Constructor for SaddleHandler.
     * Registers this class as an event listener with the given plugin instance.
     *
     * @param plugin The plugin instance that will register this listener.
     */
    public SaddleHandler(RPGHorse plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Event handler for when a player right-clicks with a saddle item.
     * This method will spawn a new horse and assign it to the player if it's not already holding one.
     * If the player already has a horse, the previous one will be removed.
     *
     * @param event The PlayerInteractEvent triggered by the player's right-click.
     */
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
            newHorse.addPassenger(player);

            playerHorses.put(playerId, newHorse);

            player.sendMessage("A new horse has been summoned for you to ride!");

            event.setCancelled(true);
        }
    }

    /**
     * Removes all currently summoned horses from all players.
     * This method will remove any horses stored in the playerHorses map.
     */
    public void removeHorses() {
        for (Horse horse: playerHorses.values()) {
            if (horse != null && !horse.isDead()) {
                horse.remove();
            }
        }

        playerHorses.clear();
    }

}
