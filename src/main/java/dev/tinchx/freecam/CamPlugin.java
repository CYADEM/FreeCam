package dev.tinchx.freecam;

import dev.tinchx.freecam.cam.FreeCam;
import dev.tinchx.freecam.config.Config;
import dev.tinchx.freecam.listener.CamListener;
import dev.tinchx.freecam.profile.Profile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CamPlugin extends JavaPlugin {

    private Config settings, messages;

    @Override
    public void onEnable() {
        prepare();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            FreeCam freeCam = Profile.get(player).getFreeCam();

            if (freeCam != null) {
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(freeCam.getLocation());

                freeCam.destroy();
            }
        });
    }

    private void prepare() {
        settings = new Config(this, "settings.yml");
        messages = new Config(this, "messages.yml");

        new CamListener();
    }

    public static CamPlugin getInstance() {
        return getPlugin(CamPlugin.class);
    }
}