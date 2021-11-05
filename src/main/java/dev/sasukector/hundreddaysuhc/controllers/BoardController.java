package dev.sasukector.hundreddaysuhc.controllers;

import dev.sasukector.hundreddaysuhc.KingOfTheHill;
import dev.sasukector.hundreddaysuhc.helpers.FastBoard;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.*;

public class BoardController {

    private static BoardController instance = null;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private @Setter @Getter boolean hideDays;

    public static BoardController getInstance() {
        if (instance == null) {
            instance = new BoardController();
        }
        return instance;
    }

    public BoardController() {
        Bukkit.getScheduler().runTaskTimer(KingOfTheHill.getInstance(), this::updateBoards, 0L, 20L);
        this.hideDays = false;
    }

    public void newPlayerBoard(Player player) {
        FastBoard board = new FastBoard(player);
        this.boards.put(player.getUniqueId(), board);
    }

    public void removePlayerBoard(Player player) {
        FastBoard board = this.boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public void updateBoards() {
        boards.forEach((uuid, board) -> {
            Player player = Bukkit.getPlayer(uuid);
            assert player != null;

            board.updateTitle("§3§lKing of the Hill");

            List<String> lines = new ArrayList<>();
            lines.add("");
            lines.add("Jugador: §6" + player.getName());

            if (TeamsController.getInstance().isMaster(player)) {
                lines.add("§bEres master");
            } else if (TeamsController.getInstance().isWinner(player)) {
                lines.add("§aHaz ganado");
            } else {
                lines.add("Asesinatos: §6" + player.getStatistic(Statistic.PLAYER_KILLS));
                lines.add("Estatus: " + (GameController.getInstance().isAlivePlayer(player) ? "§9Jugando" : "§4Esperando"));
            }

            lines.add("");
            lines.add("Online: §6" + Bukkit.getOnlinePlayers().size());
            lines.add("Ganadores: §6" + TeamsController.getInstance().getWinnerTeam().getEntries().size());
            lines.add("");

            board.updateLines(lines);
        });
    }

}
