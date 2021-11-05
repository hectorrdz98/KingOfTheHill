package dev.sasukector.hundreddaysuhc.controllers;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamsController {

    private static TeamsController instance = null;
    private @Getter Team masterTeam;
    private @Getter Team winnerTeam;

    public static TeamsController getInstance() {
        if (instance == null) {
            instance = new TeamsController();
        }
        return instance;
    }

    public TeamsController() {
        this.createOrLoadTeams();
    }

    public void createOrLoadTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        masterTeam = scoreboard.getTeam("master");
        winnerTeam = scoreboard.getTeam("winner");

        if (masterTeam == null) {
            masterTeam = scoreboard.registerNewTeam("master");
            masterTeam.color(NamedTextColor.AQUA);
            masterTeam.prefix(Component.text("♔ "));
            masterTeam.setAllowFriendlyFire(false);
        }

        if (winnerTeam == null) {
            winnerTeam = scoreboard.registerNewTeam("winner");
            winnerTeam.color(NamedTextColor.LIGHT_PURPLE);
            winnerTeam.prefix(Component.text("☀ "));
        }
    }

    public List<Player> getMasterPlayers() {
        List<Player> players = new ArrayList<>();
        this.masterTeam.getEntries().forEach(entry -> {
            Player player = Bukkit.getPlayer(entry);
            if (player != null) {
                players.add(player);
            }
        });
        return players;
    }

    public List<Player> getWinnerPlayers() {
        List<Player> players = new ArrayList<>();
        this.winnerTeam.getEntries().forEach(entry -> {
            Player player = Bukkit.getPlayer(entry);
            if (player != null) {
                players.add(player);
            }
        });
        return players;
    }

    public List<Player> getNormalPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getScoreboard().getTeams().stream()
                        .noneMatch(t -> t.getEntries().contains(p.getName())))
                .collect(Collectors.toList());
    }

    public boolean isMaster(Player player) {
        return this.getMasterPlayers().contains(player);
    }

    public boolean isWinner(Player player) {
        return this.getWinnerPlayers().contains(player);
    }

}
