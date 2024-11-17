package io.github.CodeerStudio.RPGHorse.handlers;

import io.github.CodeerStudio.RPGHorse.RPGHorse;
import io.github.CodeerStudio.RPGHorse.model.HorseData;
import io.papermc.paper.event.entity.EntityMoveEvent;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SaddleHandler implements Listener {

    private final HashMap<UUID, Horse> playerHorses = new HashMap<>();
    private final HashMap<UUID, HorseData> playerHorseData = new HashMap<>();
    private final HashMap<UUID, ItemStack> horseSaddles = new HashMap<>();
    private final int LEVEL_UP_DISTANCE = 20;

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

                // Add HorseData for the new horse
                playerHorseData.put(newHorse.getUniqueId(), new HorseData(newHorse.getLocation()));

                // Store the new horse for the player
                playerHorses.put(playerId, newHorse);
                player.sendMessage("A new horse has been summoned for you to ride!");

                horseSaddles.put(newHorse.getUniqueId(), item);

                // Cancel the event to prevent normal saddle behavior
                event.setCancelled(true);
            }
        }
    }

    /**
     * Listens for the movement of the horse to track distance traveled.
     * When the horse travels a certain distance (500 blocks), it levels up.
     *
     * @param event The EntityMoveEvent triggered when a horse moves.
     */
    @EventHandler
    public void onHorseMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if the player is riding a horse
        if (player.getVehicle() instanceof Horse) {
            Horse horse = (Horse) player.getVehicle();
            UUID horseId = horse.getUniqueId();

            // Proceed only if the horse is registered in playerHorseData
            if (playerHorseData.containsKey(horseId)) {
                HorseData horseData = playerHorseData.get(horseId);
                double distanceTraveled = horseData.getDistanceTraveled();

                // Calculate the distance moved by the player (while riding the horse)
                double distance = player.getLocation().distance(horseData.getLastPosition());

                distanceTraveled += distance;

                // If the horse has traveled enough distance, level it up
                while (distanceTraveled >= LEVEL_UP_DISTANCE) {
                    levelUpHorse(horse);  // Level up the horse
                    distanceTraveled -= LEVEL_UP_DISTANCE;  // Reduce the traveled distance by the level-up threshold
                }

                // Update the total distance traveled and store the last position
                horseData.setDistanceTraveled(distanceTraveled);
                horseData.setLastPosition(player.getLocation());  // Track player's position as the horse's location
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
                UUID horseId = previousHorse.getUniqueId();
                previousHorse.remove();

                horseSaddles.remove(horseId);
                playerHorseData.remove(horseId);

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

        playerHorseData.put(newHorse.getUniqueId(), new HorseData(newHorse.getLocation()));

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

    /**
     * Levels up the horse, increasing its stats.
     *
     * @param horse The horse to level up.
     */
    private void levelUpHorse(Horse horse) {

        double newSpeed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() + 0.01;
        double newJumpPower = horse.getJumpStrength() + 0.1;

        // Apply new stats to the horse
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newSpeed);
        horse.setJumpStrength(newJumpPower);

        Player player = (Player) horse.getOwner();

        ItemStack saddle = horseSaddles.get(horse.getUniqueId());

        if (saddle != null && saddle.getItemMeta() != null) {
            ItemMeta meta = saddle.getItemMeta();
            List<String> lore = new ArrayList<>();

            // Retrieve and increment the level from the lore
            int currentLevel = 1; // Default level
            if (meta.hasLore()) {
                for (String line : meta.getLore()) {
                    if (line.contains("Level:")) {
                        String levelStr = line.split(":")[1].trim();
                        levelStr = levelStr.replaceAll("§[0-9a-fA-Fk-or]", ""); // Remove color codes
                        try {
                            currentLevel = Integer.parseInt(levelStr);
                            currentLevel++; // Increment the level
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Error reading current level. Resetting to Level 1.");
                        }
                        break;
                    }
                }
            }

            lore.add(ChatColor.GREEN + "Level: " + ChatColor.YELLOW + currentLevel);
            lore.add(ChatColor.GREEN + "Speed: " + ChatColor.YELLOW + String.format("%.2f", newSpeed));
            lore.add(ChatColor.GREEN + "Jump Power: " + ChatColor.YELLOW + String.format("%.2f", newJumpPower));
            lore.add(ChatColor.GRAY + "Use it wisely, and your horse will grow stronger!");

            meta.setLore(lore);
            saddle.setItemMeta(meta);

            player.sendMessage(ChatColor.GREEN + "The saddle has been updated with your horse's new stats.");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to update the saddle stats. Make sure your horse has a saddle equipped.");
        }
    }
}
