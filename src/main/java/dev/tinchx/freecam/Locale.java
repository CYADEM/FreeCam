package dev.tinchx.freecam;

import dev.tinchx.freecam.utilities.ColorText;

public enum Locale {

    FORCE_DISABLED, TELEPORT_CANCELLED, CAM_ENABLED, CAM_DISABLED,RADIUS_REACHED,COMMAND_DISABLED;

    public String getMessage() {
        return ColorText.translate(CamPlugin.getInstance().getMessages().getString(name().replace("_", "-")));
    }
}