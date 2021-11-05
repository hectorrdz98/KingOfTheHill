package dev.sasukector.hundreddaysuhc.commands;

import dev.sasukector.hundreddaysuhc.controllers.TeamsController;
import dev.sasukector.hundreddaysuhc.helpers.ServerUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WinnersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), "minecraft:block.note_block.bell", 1, 1);

            StringBuilder winners = new StringBuilder();
            for (String playerName : TeamsController.getInstance().getWinnerTeam().getEntries()) {
                winners.append("\n- ").append(playerName);
            }
            ServerUtilities.sendServerMessage(player, ServerUtilities.getMiniMessage()
                    .parse("<bold><color:#F9C74F>Ganadores:</color></bold><color:#F9844A>" +
                            winners + "</color>"));
        }
        return true;
    }

}
