package dev.tinchx.freecam.cam;

import dev.tinchx.freecam.CamPlugin;
import dev.tinchx.freecam.utilities.ColorText;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;

@Getter
@RequiredArgsConstructor
public class FreeCam {

    private final Player player;
    private Location location;
    private int radius;
    private Entity entity;

    public FreeCam create() {
        location = player.getLocation();
        radius = radius();

        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(ColorText.translate("&c[Spectator] " + player.getName()));

        armorStand.setCanPickupItems(false);
        armorStand.setMetadata("freecam", new FixedMetadataValue(CamPlugin.getInstance(), player.getName()));
        armorStand.setNoDamageTicks(1000);

        armorStand.setVisible(false);

        entity = armorStand;

        player.setGameMode(GameMode.SPECTATOR);

        return this;
    }

    public void destroy() {
        entity.remove();
    }

    private int radius() {
        int min = CamPlugin.getInstance().getSettings().getInt("DEFAULT-RADIUS", 20),
                max = CamPlugin.getInstance().getSettings().getInt("MAX-RADIUS", 100);

        for (int i = min; i < max; i++) {
            if (player.hasPermission("freecam.radius." + i)) {
                radius = i;
                break;
            }
        }

        if (radius > max) {
            radius = max;
        } else if (radius < min) {
            radius = min;
        }

        return radius;
    }
}