package dev.tinchx.freecam.utilities;

import org.bukkit.ChatColor;

public class ColorText {

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}