package dev.tinchx.freecam.listener;

import dev.tinchx.freecam.CamPlugin;
import dev.tinchx.freecam.Locale;
import dev.tinchx.freecam.cam.FreeCam;
import dev.tinchx.freecam.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;

public final class CamListener implements Listener {

    public CamListener() {
        Bukkit.getPluginManager().registerEvents(this, CamPlugin.getInstance());
    }

    @EventHandler
    final void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.get(player);
        FreeCam freeCam = profile.getFreeCam();

        String[] args = event.getMessage().split(" ");

        String permission = CamPlugin.getInstance().getSettings().getString("PERMISSION");

        if (args[0].equalsIgnoreCase("/freecam")) {
            if (permission != null && !permission.equals("") && !player.hasPermission(permission)) {
                return;
            }
            event.setCancelled(true);


            if (freeCam != null) {
                player.teleport(freeCam.getLocation());
                player.setGameMode(GameMode.SURVIVAL);
                freeCam.destroy();

                player.sendMessage(Locale.CAM_DISABLED.getMessage());

                profile.setFreeCam(null);
            } else {
                profile.setFreeCam(new FreeCam(player).create());

                player.sendMessage(Locale.CAM_ENABLED.getMessage());
            }
        } else {
            if (freeCam != null) {
                for (String s : CamPlugin.getInstance().getSettings().getStringList("DISABLED-COMMANDS")) {
                    if (args[0].equalsIgnoreCase(s)) {
                        event.setCancelled(true);
                        player.sendMessage(Locale.COMMAND_DISABLED.getMessage());
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    final void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager) {
            Villager villager = (Villager) event.getEntity();

            if (villager.hasMetadata("freecam")) {
                event.setCancelled(true);

                String owner = null;

                for (MetadataValue value : villager.getMetadata("freecam")) {
                    owner = value.asString();
                }

                if (owner == null) {
                    villager.remove();
                    return;
                }

                Player player = Bukkit.getPlayerExact(owner);

                if (player != null) {
                    Profile profile = Profile.get(player);
                    FreeCam freeCam = profile.getFreeCam();

                    player.setGameMode(GameMode.SURVIVAL);
                    if (freeCam != null) {
                        player.teleport(freeCam.getLocation());
                        player.sendMessage(Locale.FORCE_DISABLED.getMessage());

                        freeCam.destroy();

                        profile.setFreeCam(null);
                    }
                }
            }
        }
    }

    @EventHandler
    final void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE && Profile.get(player).getFreeCam() != null) {
            event.setCancelled(true);
            player.sendMessage(Locale.TELEPORT_CANCELLED.getMessage());
        }
    }

    @EventHandler
    final void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        FreeCam freeCam = Profile.get(player).getFreeCam();

        if (freeCam != null) {
            if (freeCam.getLocation().distanceSquared(to) <= freeCam.getRadius()) {
                return;
            }

            event.setTo(from);
            player.sendMessage(Locale.RADIUS_REACHED.getMessage());
        }
    }

    @EventHandler
    final void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FreeCam freeCam = Profile.get(player).getFreeCam();

        if (freeCam != null) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(freeCam.getLocation());
            freeCam.destroy();
        }
    }


}
