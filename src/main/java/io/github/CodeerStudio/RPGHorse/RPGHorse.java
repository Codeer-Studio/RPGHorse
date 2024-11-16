package io.github.CodeerStudio.RPGHorse;

import io.github.CodeerStudio.RPGHorse.commands.Saddle;
import io.github.CodeerStudio.RPGHorse.handlers.PlayerHandler;
import io.github.CodeerStudio.RPGHorse.handlers.SaddleHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPGHorse extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Hello world!");

        getCommand("saddle").setExecutor(new Saddle());

        new SaddleHandler(this);
        new PlayerHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting down!");
    }
}
