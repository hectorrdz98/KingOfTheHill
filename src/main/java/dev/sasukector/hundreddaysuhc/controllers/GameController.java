package dev.sasukector.hundreddaysuhc.controllers;

import dev.sasukector.hundreddaysuhc.KingOfTheHill;
import dev.sasukector.hundreddaysuhc.helpers.ServerUtilities;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController {

    private static GameController instance = null;
    private @Getter @Setter boolean gameStarted;
    private final @Getter List<UUID> alivePlayers;

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public GameController() {
        this.gameStarted = false;
        this.alivePlayers = new ArrayList<>();
    }

    public boolean isAlivePlayer(Player player) {
        return this.alivePlayers.contains(player.getUniqueId());
    }

    public void restartPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.setArrowsInBody(0);
        player.setFireTicks(0);
        player.setVisualFire(false);
        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));
        player.getInventory().clear();
        player.updateInventory();
    }

    public void givePlayerKit(Player player) {
        ItemStack stick = new ItemStack(Material.STICK);
        stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
        ItemMeta stickMeta = stick.getItemMeta();
        stickMeta.displayName(Component.text("Manolo", TextColor.color(0x90BE6D)));
        stick.setItemMeta(stickMeta);
        player.getInventory().addItem(stick);

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 4);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.displayName(Component.text("Otro manolo", TextColor.color(0xF9844A)));
        bow.setItemMeta(bowMeta);
        player.getInventory().addItem(bow);

        player.getInventory().addItem(new ItemStack(Material.COOKED_COD, 15));
        player.getInventory().addItem(new ItemStack(Material.ARROW, 3));
        player.updateInventory();
    }

    public void handlePlayerJoin(Player player) {
        if (this.isGameStarted()) {
            if (!this.alivePlayers.contains(player.getUniqueId())) {
                player.setGameMode(GameMode.SPECTATOR);
                this.restartPlayer(player);
            }
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            this.restartPlayer(player);
            player.teleport(ServerUtilities.getLobbySpawn());
        }
    }

    public void handlePlayerLeave(Player player) {
        if (this.alivePlayers.contains(player.getUniqueId())) {
            ServerUtilities.sendBroadcastMessage(
                    Component.text(player.getName() + " ha abandonado...", TextColor.color(0xF94144))
            );
            this.alivePlayers.remove(player.getUniqueId());
        }
    }

    public void handlePlayerDeath(Player player) {
        this.restartPlayer(player);
        player.teleport(ServerUtilities.getLobbySpawn());
        Bukkit.getScheduler().runTaskLater(KingOfTheHill.getInstance(), () -> this.givePlayerKit(player), 5L);
    }

    public void onPlayerScore(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        this.restartPlayer(player);
        this.alivePlayers.remove(player.getUniqueId());
        TeamsController.getInstance().getWinnerTeam().addEntry(player.getName());
        ServerUtilities.sendBroadcastMessage(ServerUtilities.getMiniMessage().parse(
                "<bold><color:#90BE6D>" + player.getName() + "</color></bold> <color:#4D908E>ha puntuado</color>"
        ));
        Bukkit.getOnlinePlayers().forEach(p ->
                p.showTitle(Title.title(Component.text("Fin del juego", TextColor.color(0x43AA8B)), Component.empty())));
        this.gameStop();
    }

    public void gameStart() {
        List<Player> players = TeamsController.getInstance().getNormalPlayers();
        this.alivePlayers.clear();
        this.gameStarted = false;
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            offlinePlayer.setStatistic(Statistic.PLAYER_KILLS, 0);
        }
        players.forEach(p -> {
            this.alivePlayers.add(p.getUniqueId());
            p.setStatistic(Statistic.PLAYER_KILLS, 0);
            p.setGameMode(GameMode.SURVIVAL);
            this.restartPlayer(p);
            this.givePlayerKit(p);
            p.teleport(ServerUtilities.getLobbySpawn());
            p.playSound(p.getLocation(), "minecraft:block.note_block.xylophone", 1, 1);
        });
        World overworld = ServerUtilities.getOverworld();
        if (overworld != null) {
            overworld.getEntities().forEach(entity -> {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                }
            });
        }
        this.startCountDown();
    }

    public void gameStop() {
        List<Player> players = TeamsController.getInstance().getNormalPlayers();
        this.alivePlayers.clear();
        this.gameStarted = false;
        players.forEach(p -> {
            p.setGameMode(GameMode.SPECTATOR);
            this.restartPlayer(p);
            p.playSound(p.getLocation(), "minecraft:block.note_block.xylophone", 1, 1);
        });
        World overworld = ServerUtilities.getOverworld();
        if (overworld != null) {
            overworld.getEntities().forEach(entity -> {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                }
            });
        }
    }

    public void startCountDown() {
        AtomicInteger remainingTime = new AtomicInteger(5);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime.get() <= 0) {
                    gameStarted = true;
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        p.showTitle(Title.title(Component.text("¡Escala!", TextColor.color(0x43AA8B)), Component.empty()));
                        p.playSound(p.getLocation(), "minecraft:entity.wither.spawn", 1, 1.6f);
                    });
                    cancel();
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (remainingTime.get() <= 3) {
                            p.showTitle(Title.title(Component.text(remainingTime.get(), TextColor.color(0x43AA8B)), Component.empty()));
                            p.playSound(p.getLocation(), "minecraft:block.note_block.xylophone", 1, 1);
                        }
                        p.sendActionBar(
                                Component.text("PVP habilitado en: " + remainingTime.get() + "s", TextColor.color(0xF3722C))
                        );
                    });
                    remainingTime.addAndGet(-1);
                }
            }
        }.runTaskTimer(KingOfTheHill.getInstance(), 0L, 20L);
    }

}
