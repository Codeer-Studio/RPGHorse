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

            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                if (meta.hasDisplayName() && meta.getDisplayName().contains("Basic Horse")) {
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

                    if (meta.hasLore()) {
                        List<String> lore = meta.getLore();
                        double speed = 0;
                        double jumpPower = 0;

                        for (String line : lore) {
                            if (line.contains("Speed:")) {
                                try {
                                    // Remove color codes and extra spaces, then extract the speed value
                                    String speedValue = line.split(":")[1].trim();
                                    speedValue = speedValue.replaceAll("§[0-9a-fA-Fk-or]", ""); // Remove color codes
                                    speed = Double.parseDouble(speedValue); // Try to parse the speed value
                                } catch (NumberFormatException e) {
                                    // Handle the error gracefully if speed can't be parsed
                                    player.sendMessage(ChatColor.RED + "Error parsing speed value. Please check the format.");
                                }
                            } else if (line.contains("Jump Power:")) {
                                try {
                                    // Remove color codes and extra spaces, then extract the jump power value
                                    String jumpPowerValue = line.split(":")[1].trim();
                                    jumpPowerValue = jumpPowerValue.replaceAll("§[0-9a-fA-Fk-or]", ""); // Remove color codes
                                    jumpPower = Double.parseDouble(jumpPowerValue); // Try to parse the jump power value
                                } catch (NumberFormatException e) {
                                    // Handle the error gracefully if jump power can't be parsed
                                    player.sendMessage(ChatColor.RED + "Error parsing jump power value. Please check the format.");
                                }
                            }
                            newHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed + 0.25);
                            newHorse.setJumpStrength(jumpPower);
                        }

                        playerHorses.put(playerId, newHorse);

                        player.sendMessage("A new horse has been summoned for you to ride!");

                        event.setCancelled(true);
                    }
                }
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

}
