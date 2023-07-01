package dev.cubesicle.bettermanhunt;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class ManhuntCompassListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !ManhuntCompass.isManhuntCompass(item)) return;
        if (HunterList.getOnline().noneMatch(player::equals)) {
            player.sendMessage(ChatColor.RED + "You are not a hunter!");
            return;
        }
        ManhuntCompass compass = new ManhuntCompass(item);

        switch (action) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                if (!player.isSneaking()) handleLeftClick(player, compass);
                else handleSneakLeftClick(player, compass);
            }
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                if (!player.isSneaking()) handleRightClick(player, compass);
                else handleSneakRightClick(player, compass);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (!ManhuntCompass.isManhuntCompass(item)) return;
        if (HunterList.getOnline().noneMatch(player::equals)) {
            item.setAmount(0);
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ManhuntCompass.resetCompass(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ManhuntCompass.resetCompass(event.getPlayer());
    }

    private void handleLeftClick(Player player, ManhuntCompass compass) {
        boolean result = compass.toggleTrackingMode(player);
        if (result) player.sendMessage(ChatColor.GREEN + "Tracking mode set to " + (compass.getCompassData().isTrackingHunters() ? "hunters." : "speedrunners."));
        else player.sendMessage(ChatColor.RED + "No players on the other team!");
    }

    private void handleSneakLeftClick(Player player, ManhuntCompass compass) {
        boolean result = compass.cycleTargets(player, -1);
        if (result) player.sendMessage(ChatColor.GREEN + "Selected " + compass.getCompassData().getSelectedTarget().getName() + " as the tracking target.");
        else player.sendMessage(ChatColor.RED + "No players to select!");
    }

    private void handleRightClick(Player player, ManhuntCompass compass) {
        boolean result = compass.updateTargetPosition();
        if (result) player.sendMessage(ChatColor.GREEN + "Updated the position of " + compass.getCompassData().getSelectedTarget().getName() + ".");
        else player.sendMessage(ChatColor.RED + "No player selected!");
    }

    private void handleSneakRightClick(Player player, ManhuntCompass compass) {
        boolean result = compass.cycleTargets(player, 1);
        if (result) player.sendMessage(ChatColor.GREEN + "Selected " + compass.getCompassData().getSelectedTarget().getName() + " as the tracking target.");
        else player.sendMessage(ChatColor.RED + "No players to select!");
    }
}
