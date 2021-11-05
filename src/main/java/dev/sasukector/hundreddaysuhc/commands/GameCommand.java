package dev.sasukector.hundreddaysuhc.commands;

import dev.sasukector.hundreddaysuhc.controllers.GameController;
import dev.sasukector.hundreddaysuhc.controllers.TeamsController;
import dev.sasukector.hundreddaysuhc.helpers.ServerUtilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GameCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && player.isOp()) {
            if (args.length > 0) {
                String option = args[0];
                if (validOptions().contains(option)) {
                    switch (option) {
                        case "start" -> {
                            if (!GameController.getInstance().isGameStarted()) {
                                GameController.getInstance().gameStart();
                            } else {
                                ServerUtilities.sendServerMessage(player, "§cEl juego está en curso");
                            }
                        }
                        case "stop" -> {
                            if (GameController.getInstance().isGameStarted()) {
                                GameController.getInstance().gameStop();
                            } else {
                                ServerUtilities.sendServerMessage(player, "§cNo hay juego en curso");
                            }
                        }
                        case "reload" -> {
                            player.playSound(player.getLocation(), "minecraft:block.note_block.xylophone", 1, 1);
                            Set<String> entries = TeamsController.getInstance().getWinnerTeam().getEntries();
                            for (String entry : entries) {
                                TeamsController.getInstance().getWinnerTeam().removeEntry(entry);
                            }
                            ServerUtilities.sendServerMessage(player,
                                    Component.text("Se reiniciaron los ganadores", TextColor.color(0x43AA8B)));
                        }
                    }
                } else {
                    ServerUtilities.sendServerMessage(player, "§cOpción no válida");
                }
            } else {
                ServerUtilities.sendServerMessage(player, "§cSelecciona una opción");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if(sender instanceof Player) {
            if (args.length == 1) {
                String partialItem = args[0];
                StringUtil.copyPartialMatches(partialItem, validOptions(), completions);
            }
        }

        Collections.sort(completions);

        return completions;
    }

    public List<String> validOptions() {
        List<String> valid = new ArrayList<>();
        valid.add("start");
        valid.add("stop");
        valid.add("reload");
        Collections.sort(valid);
        return valid;
    }

}
