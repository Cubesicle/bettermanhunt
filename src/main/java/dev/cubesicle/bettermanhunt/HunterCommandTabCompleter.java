package dev.cubesicle.bettermanhunt;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HunterCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) return List.of("add", "remove", "list");
        if (args.length == 2) return switch (args[0]) {
            case "add" -> Bukkit.getOnlinePlayers().stream().filter(player -> HunterList.getOnline().noneMatch(player::equals)).map(Player::getName).toList();
            case "remove" -> HunterList.getOffline().map(OfflinePlayer::getName).toList();
            default -> List.of("");
        };
        return List.of("");
    }
}
