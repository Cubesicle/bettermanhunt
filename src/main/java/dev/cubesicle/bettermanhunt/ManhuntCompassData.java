package dev.cubesicle.bettermanhunt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class ManhuntCompassData {
    private static final NamespacedKey LAST_TRACKED = new NamespacedKey(Main.getInstance(), "last_tracked");
    private static final NamespacedKey SELECTED_TARGET = new NamespacedKey(Main.getInstance(), "selected_target");
    private static final NamespacedKey IS_TRACKING_HUNTERS = new NamespacedKey(Main.getInstance(), "is_tracking_hunters");
    private final PersistentDataContainer PDC;

    public ManhuntCompassData() {
        PDC = defaultCompassData();
    }

    public ManhuntCompassData(PersistentDataContainer pdc) {
        String lastTracked = pdc.getOrDefault(LAST_TRACKED, PersistentDataType.STRING, "");
        String selectedTarget = pdc.getOrDefault(SELECTED_TARGET, PersistentDataType.STRING, "");
        boolean isTrackingHunters = pdc.getOrDefault(IS_TRACKING_HUNTERS, PersistentDataType.BOOLEAN, false);

        PersistentDataContainer compassData = defaultCompassData();
        compassData.set(LAST_TRACKED, PersistentDataType.STRING, lastTracked);
        compassData.set(SELECTED_TARGET, PersistentDataType.STRING, selectedTarget);
        compassData.set(IS_TRACKING_HUNTERS, PersistentDataType.BOOLEAN, isTrackingHunters);

        PDC = compassData;
    }

    public PersistentDataContainer getPDC() {
        return PDC;
    }

    public Player getLastTracked() {
        String lastTrackedUUIDString = Objects.requireNonNull(PDC.get(LAST_TRACKED, PersistentDataType.STRING));
        return getPlayerFromUUIDString(lastTrackedUUIDString);
    }

    public void setLastTracked(Player lastTracked) {
        PDC.set(LAST_TRACKED, PersistentDataType.STRING, lastTracked.getUniqueId().toString());
    }

    public Player getSelectedTarget() {
        String targetUUIDString = Objects.requireNonNull(PDC.get(SELECTED_TARGET, PersistentDataType.STRING));
        return getPlayerFromUUIDString(targetUUIDString);
    }

    public void setSelectedTarget(Player target) {
        PDC.set(SELECTED_TARGET, PersistentDataType.STRING, target.getUniqueId().toString());
    }

    public boolean isTrackingHunters() {
        return Boolean.TRUE.equals(PDC.get(IS_TRACKING_HUNTERS, PersistentDataType.BOOLEAN));
    }

    public void setTrackingHunters(boolean bool) {
        PDC.set(IS_TRACKING_HUNTERS, PersistentDataType.BOOLEAN, bool);
    }

    private Player getPlayerFromUUIDString(String uuidString) {
        UUID targetUUID;
        try {
            targetUUID = UUID.fromString(uuidString);
        } catch(IllegalArgumentException e ) {
            return null;
        }

        return Bukkit.getPlayer(targetUUID);
    }

    private PersistentDataContainer defaultCompassData() {
        ItemMeta meta = Objects.requireNonNull(new ItemStack(Material.COMPASS).getItemMeta());
        PersistentDataContainer compassData = meta.getPersistentDataContainer();
        compassData.set(LAST_TRACKED, PersistentDataType.STRING, "");
        compassData.set(SELECTED_TARGET, PersistentDataType.STRING, "");
        compassData.set(IS_TRACKING_HUNTERS, PersistentDataType.BOOLEAN, false);

        return compassData;
    }
}
