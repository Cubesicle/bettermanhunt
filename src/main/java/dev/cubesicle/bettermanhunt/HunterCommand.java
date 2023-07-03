package dev.cubesicle.bettermanhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HunterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Console must provide args.");
                return true;
            }
            if (!HunterList.add(player.getUniqueId())) HunterList.remove(player.getUniqueId());
            return true;
        }

        return switch (args[0]) {
            case "add" -> add(sender, args);
            case "remove" -> remove(sender, args);
            case "list" -> list(sender, args);
            default -> false;
        };
    }

    private boolean add(CommandSender sender, String[] args) {
        if (args.length != 2) return false;

        String playerName = args[1];

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Could not find " + playerName + ".");
            return true;
        }

        if (!HunterList.add(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already a hunter.");
            return true;
        }

        if (target != sender) sender.sendMessage(ChatColor.GREEN + target.getName() + " is now a hunter.");
        return true;
    }

    private boolean remove(CommandSender sender, String[] args) {
        if (args.length != 2) return false;

        String playerName = args[1];

        Optional<OfflinePlayer> target = HunterList.getOffline().filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(playerName)).findFirst();

        if (target.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Could not find " + playerName + ".");
            return true;
        }

        if (!HunterList.remove(target.get().getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.get().getName() + " is not a hunter.");
            return true;
        }

        if (target.get() != sender) sender.sendMessage(ChatColor.GREEN + target.get().getName() + " is no longer a hunter.");
        return true;
    }

    private boolean list(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        String names = String.join(", ", HunterList.getOffline().map(p -> p.getName() + (!p.isOnline() ? " (offline)" : "")).toList());

        if (names.equals("")) sender.sendMessage(ChatColor.RED + "There are no hunters.");
        else sender.sendMessage(ChatColor.GOLD + "Hunters: " + ChatColor.RESET + names);

        return true;
    }
}