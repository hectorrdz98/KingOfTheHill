package dev.sasukector.hundreddaysuhc;

import dev.sasukector.hundreddaysuhc.commands.WinnersCommand;
import dev.sasukector.hundreddaysuhc.commands.GameCommand;
import dev.sasukector.hundreddaysuhc.controllers.BoardController;
import dev.sasukector.hundreddaysuhc.events.SpawnEvents;
import dev.sasukector.hundreddaysuhc.helpers.ServerUtilities;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class KingOfTheHill extends JavaPlugin {

    private static @Getter
    KingOfTheHill instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info(ChatColor.DARK_PURPLE + "KingOfTheHill startup!");
        instance = this;

        // Register events
        this.getServer().getPluginManager().registerEvents(new SpawnEvents(), this);
        Bukkit.getOnlinePlayers().forEach(player -> BoardController.getInstance().newPlayerBoard(player));

        // Register commands
        Objects.requireNonNull(KingOfTheHill.getInstance().getCommand("winners")).setExecutor(new WinnersCommand());
        Objects.requireNonNull(KingOfTheHill.getInstance().getCommand("game")).setExecutor(new GameCommand());

        // Set lobby spawn
        ServerUtilities.setLobbySpawn(new Location(ServerUtilities.getOverworld(), 275, 74, 215));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(ChatColor.DARK_PURPLE + "KingOfTheHill shutdown!");
    }
}
