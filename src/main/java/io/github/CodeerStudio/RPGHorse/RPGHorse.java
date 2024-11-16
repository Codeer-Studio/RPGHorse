package io.github.CodeerStudio.RPGHorse;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPGHorse extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Hello world!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting down!");
    }
}
