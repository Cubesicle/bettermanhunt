package dev.cubesicle.bettermanhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class HunterList {
    private static final HashSet<UUID> LIST = new HashSet<>();

    public static boolean add(UUID uuid) {
        if (!LIST.add(uuid)) return false;

        Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
        player.sendMessage(ChatColor.GREEN + "You are now a hunter.");
        ManhuntCompass.resetCompass(player);

        return true;
    }

    public static boolean remove(Object o) {
        if (!LIST.remove((UUID) o)) return false;

        Player player = Bukkit.getPlayer((UUID) o);
        if (player == null) return true;

        player.sendMessage(ChatColor.RED + "You are no longer a hunter.");
        ManhuntCompass.resetCompass(player);

        return true;
    }

    public static Stream<Player> getOnline() {
        return LIST.stream().map(Bukkit::getPlayer).filter(Objects::nonNull);
    }

    public static Stream<OfflinePlayer> getOffline() {
        return LIST.stream().map(Bukkit::getOfflinePlayer);
    }
}
