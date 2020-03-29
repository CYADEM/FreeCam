package dev.tinchx.freecam.profile;

import com.google.common.collect.Maps;
import dev.tinchx.freecam.cam.FreeCam;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@Setter
public class Profile {

    private static Map<UUID, Profile> profileMap = Maps.newHashMap();

    private long delay;
    @Getter
    private FreeCam freeCam;

    public Profile(UUID uuid) {
        profileMap.put(uuid, this);
    }

    public long getDelay() {
        return delay - System.currentTimeMillis();
    }

    public static Profile get(Player player) {
        Profile profile = profileMap.get(player.getUniqueId());
        if (profile == null) {
            profile = new Profile(player.getUniqueId());
        }
        return profile;
    }
}