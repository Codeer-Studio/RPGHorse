package io.github.CodeerStudio.RPGHorse.handlers;

import io.github.CodeerStudio.RPGHorse.RPGHorse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
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
     * If the player is holding a custom saddle (with the display name "Basic Horse"),
     * this method will spawn a new horse and assign it to the player. If the player already has a horse,
     * the previous one will be removed before spawning the new horse.
     *
     * The horse's stats (speed and jump power) are extracted from the saddle's lore and applied to the new horse.
     *
     * @param event The PlayerInteractEvent triggered by the player's right-click action.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSaddleUse_Normal(PlayerInteractEvent event) {
        // Ensure the event is a right-click action and the item is a saddle
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        ItemStack item = event.getItem();

        // Check if the item is a saddle and has custom metadata
        if (item != null && item.getType() == Material.SADDLE) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName() && meta.getDisplayName().contains("Basic Horse")) {
                Player player = event.getPlayer();
                UUID playerId = player.getUniqueId();

                // Handle despawning previous horse if it exists
                despawnPreviousHorseIfExists(playerId, player);

                // Spawn a new horse and apply customization
                Horse newHorse = spawnAndCustomizeHorse(player);

                // Process the saddle's lore and apply stats (speed, jump power)
                if (meta.hasLore()) {
                    processSaddleLore(meta.getLore(), newHorse, player);
                }

                // Store the new horse for the player
                playerHorses.put(playerId, newHorse);
                player.sendMessage("A new horse has been summoned for you to ride!");

                // Cancel the event to prevent normal saddle behavior
                event.setCancelled(true);
            }
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

    /**
     * Checks if a player already has a summoned horse and removes it if it exists.
     * Sends a message to the player notifying them that their previous horse has been despawned.
     *
     * @param playerId The UUID of the player to check for a previous horse.
     * @param player The player who triggered the event.
     */
    private void despawnPreviousHorseIfExists(UUID playerId, Player player) {
        if (playerHorses.containsKey(playerId)) {
            Horse previousHorse = playerHorses.get(playerId);

            if (previousHorse != null && !previousHorse.isDead()) {
                previousHorse.remove();
                player.sendMessage("Your previous horse has been despawned");
            }
        }
    }

    /**
     * Spawns a new horse for the player and applies the necessary customizations.
     * The horse is tamed, given a custom name, and assigned to the player.
     *
     * @param player The player who triggered the event.
     * @return The newly spawned and customized horse.
     */
    private Horse spawnAndCustomizeHorse(Player player) {
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

        return newHorse;
    }

    /**
     * Parses the lore of the saddle item to extract the speed and jump power values.
     * These values are then applied to the new horse to adjust its stats.
     *
     * @param lore The list of lore strings from the saddle item meta.
     * @param newHorse The newly spawned horse to which the stats will be applied.
     * @param player The player who triggered the event.
     */
    private void processSaddleLore(List<String> lore, Horse newHorse, Player player) {
        double speed = 0.0;
        double jumpPower = 1.0;

        for (String line : lore) {
            if (line.contains("Speed:")) {
                try {
                    // Extract and parse the speed value
                    String speedValue = line.split(":")[1].trim();
                    speedValue = speedValue.replaceAll("§[0-9a-fA-Fk-or]", ""); // Remove color codes
                    speed = Double.parseDouble(speedValue);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Error parsing speed value. Please check the format.");
                }
            } else if (line.contains("Jump Power:")) {
                try {
                    // Extract and parse the jump power value
                    String jumpPowerValue = line.split(":")[1].trim();
                    jumpPowerValue = jumpPowerValue.replaceAll("§[0-9a-fA-Fk-or]", ""); // Remove color codes
                    jumpPower = Double.parseDouble(jumpPowerValue);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Error parsing jump power value. Please check the format.");
                }
            }
        }

        // Apply the parsed stats to the horse
        newHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed + 0.25); // Adjust speed relative to default speed
        newHorse.setJumpStrength(jumpPower);
    }
}
