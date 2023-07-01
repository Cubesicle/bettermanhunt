package dev.cubesicle.bettermanhunt;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ManhuntCompass {
    private static final NamespacedKey COMPASS_DATA_KEY = new NamespacedKey(Main.getInstance(), "compass_data");
    private final ItemStack COMPASS;

    public ManhuntCompass() {
        COMPASS = new ItemStack(Material.COMPASS);
        COMPASS.setItemMeta(defaultMeta());
        updateLore();
    }

    public ManhuntCompass(ItemStack itemStack) {
        COMPASS = itemStack;

        if (!isManhuntCompass(itemStack)) {
            COMPASS.setType(Material.COMPASS);
            COMPASS.setItemMeta(defaultMeta());
        }

        updateLore();
    }

    public static boolean isManhuntCompass(ItemStack itemStack) {
        boolean isCompass = itemStack.getType() == Material.COMPASS;

        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        boolean hasCompassData = meta.getPersistentDataContainer().has(COMPASS_DATA_KEY, PersistentDataType.TAG_CONTAINER);

        return isCompass && hasCompassData;
    }

    public static void resetCompass(Player player) {
        player.getInventory().forEach(itemStack -> {
            if (itemStack != null && isManhuntCompass(itemStack)) itemStack.setAmount(0);
        });
        if (HunterList.getOnline().anyMatch(player::equals)) player.getInventory().addItem(new ManhuntCompass().getItemStack());
    }

    public ItemStack getItemStack() {
        return COMPASS;
    }

    public ManhuntCompassData getCompassData() {
        ItemMeta meta = Objects.requireNonNull(COMPASS.getItemMeta());
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return new ManhuntCompassData(Objects.requireNonNull(pdc.get(COMPASS_DATA_KEY, PersistentDataType.TAG_CONTAINER)));
    }

    public boolean toggleTrackingMode(Player exclude) {
        ManhuntCompassData compassData = getCompassData();
        compassData.setTrackingHunters(!compassData.isTrackingHunters());

        List<? extends Player> targetList;
        Comparator<Player> distanceComparator = Comparator.comparingDouble(player -> {
            Location location = player.getLocation();
            location.setWorld(exclude.getWorld());
            return location.distance(exclude.getLocation());
        });
        if (compassData.isTrackingHunters()) {
            targetList = HunterList.getOnline()
                    .filter(player -> !player.equals(exclude))
                    .sorted(distanceComparator)
                    .toList();
        } else {
            targetList = Bukkit.getOnlinePlayers().stream()
                    .filter(player -> HunterList.getOnline().noneMatch(player::equals))
                    .sorted(distanceComparator)
                    .toList();
        }
        if (targetList.size() < 1) return false;

        compassData.setSelectedTarget(targetList.get(0));
        setCompassData(compassData);

        return true;
    }

    public boolean cycleTargets(Player exclude, int increment) {
        ManhuntCompassData compassData = getCompassData();

        List<? extends Player> targetList;
        if (compassData.isTrackingHunters()) {
            targetList = HunterList.getOnline().filter(player -> !player.equals(exclude)).toList();
        } else {
            targetList = Bukkit.getOnlinePlayers().stream().filter(player -> HunterList.getOnline().noneMatch(player::equals)).toList();
        }
        if (targetList.size() < 1) return false;

        int currentIndex = Math.max(0, targetList.indexOf(compassData.getSelectedTarget()));
        Player target = targetList.get(Math.floorMod(currentIndex + increment, targetList.size()));

        compassData.setSelectedTarget(target);
        setCompassData(compassData);

        return true;
    }

    public boolean updateTargetPosition() {
        ManhuntCompassData compassData = getCompassData();
        Player target = compassData.getSelectedTarget();
        if (target == null) return false;

        compassData.setLastTracked(target);
        setCompassData(compassData);

        CompassMeta meta = Objects.requireNonNull((CompassMeta) COMPASS.getItemMeta());
        meta.setLodestone(target.getLocation());
        meta.setLodestoneTracked(false);
        COMPASS.setItemMeta(meta);

        return true;
    }

    private void setCompassData(ManhuntCompassData compassData) {
        ItemMeta meta = Objects.requireNonNull(COMPASS.getItemMeta());
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(COMPASS_DATA_KEY, PersistentDataType.TAG_CONTAINER, compassData.getPDC());
        COMPASS.setItemMeta(meta);
        updateLore();
    }

    private void updateLore() {
        ItemMeta meta = Objects.requireNonNull(COMPASS.getItemMeta());

        Player lastTracked = getCompassData().getLastTracked();
        String lastTrackedName = lastTracked != null ? ChatColor.GREEN + lastTracked.getName() : ChatColor.RED + "no one";

        Player target = getCompassData().getSelectedTarget();
        String targetName = target != null ? ChatColor.GREEN + target.getName() : ChatColor.RED + "no one";

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "Left-click changes the tracking mode.");
        lore.add(ChatColor.DARK_GRAY + "Right-click updates the target's position.");
        lore.add(ChatColor.DARK_GRAY + "Crouch + left/right-click cycles between the targets.");
        lore.add(ChatColor.GRAY + "Last tracked: " + lastTrackedName);
        lore.add(ChatColor.GRAY + "Selected target: " + targetName);
        lore.add(ChatColor.GRAY + "Selection mode: " + ChatColor.GREEN + (getCompassData().isTrackingHunters() ? "hunters" : "speedrunners"));

        meta.setLore(lore);
        COMPASS.setItemMeta(meta);
    }

    private ItemMeta defaultMeta() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) Objects.requireNonNull(compass.getItemMeta());
        meta.setDisplayName(ChatColor.RED + "Manhunt Compass");
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLodestoneTracked(true);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(COMPASS_DATA_KEY, PersistentDataType.TAG_CONTAINER, new ManhuntCompassData().getPDC());

        return meta;
    }
}
