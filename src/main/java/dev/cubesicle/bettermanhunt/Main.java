package dev.cubesicle.bettermanhunt;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {
    private static Plugin instance;
    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("BetterManhunt enabled.");
        getServer().getPluginManager().registerEvents(new ManhuntCompassListener(), this);

        PluginCommand hunterCommand = Objects.requireNonNull(getCommand("hunter"));
        hunterCommand.setExecutor(new HunterCommand());
        hunterCommand.setTabCompleter(new HunterCommandTabCompleter());
    }

    @Override
    public void onDisable() {
        getLogger().info("BetterManhunt disabled.");
    }

    public static Plugin getInstance() {
        return instance;
    }
}
