package dev.sasukector.hundreddaysuhc.events;

import dev.sasukector.hundreddaysuhc.controllers.BoardController;
import dev.sasukector.hundreddaysuhc.controllers.GameController;
import dev.sasukector.hundreddaysuhc.controllers.TeamsController;
import dev.sasukector.hundreddaysuhc.helpers.ServerUtilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SpawnEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(
                Component.text("+ ", TextColor.color(0x84E3A4))
                        .append(Component.text(player.getName(), TextColor.color(0x84E3A4)))
        );
        BoardController.getInstance().newPlayerBoard(player);
        GameController.getInstance().handlePlayerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BoardController.getInstance().removePlayerBoard(player);
        event.quitMessage(
                Component.text("- ", TextColor.color(0xE38486))
                        .append(Component.text(player.getName(), TextColor.color(0xE38486)))
        );
        GameController.getInstance().handlePlayerLeave(player);
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!GameController.getInstance().isGameStarted()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!GameController.getInstance().isGameStarted()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void blockChestInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(block != null && block.getState() instanceof InventoryHolder) {
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                    event.setCancelled(true);
            } else if (block != null && block.getType() == Material.DIAMOND_BLOCK) {
                Player player = event.getPlayer();
                if (TeamsController.getInstance().getNormalPlayers().contains(player)) {
                    GameController.getInstance().onPlayerScore(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onDroppedItem(PlayerDropItemEvent event) {
        if (!GameController.getInstance().isGameStarted()) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemEaten(PlayerItemConsumeEvent event) {
        if (!GameController.getInstance().isGameStarted()) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickedUpItems(PlayerAttemptPickupItemEvent event) {
        if (!GameController.getInstance().isGameStarted()) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ServerUtilities.sendBroadcastMessage(Component.text(player.getName() + " ha muerto...", TextColor.color(0xF94144)));
        event.deathMessage(Component.empty());
        event.getDrops().clear();
        GameController.getInstance().handlePlayerDeath(player);
    }

    @EventHandler
    public void onPlayerKillPlayer(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player && player.getKiller() != null) {
            Player killer = player.getKiller();
            ServerUtilities.sendServerMessage(killer, ServerUtilities.getMiniMessage().parse(
                    "<color:#4D908E>Mataste a</color> <bold><color:#90BE6D></color></bold>"
            ));
            killer.getInventory().addItem(new ItemStack(Material.ARROW));
        }
    }

}
