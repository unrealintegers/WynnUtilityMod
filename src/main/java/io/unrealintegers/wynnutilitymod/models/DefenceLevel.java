package io.unrealintegers.wynnutilitymod.models;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;

import java.awt.*;
import java.util.Locale;

public enum DefenceLevel {
    VERY_LOW(new Color(0x55ffff), "-"),
    LOW(new Color(0x55ff55), "L"),
    MEDIUM(new Color(0xffff55), "M"),
    HIGH(new Color(0xff5555), "H"),
    VERY_HIGH(new Color(0xaa00aa), "V");

    public static DefenceLevel parse(String level) {
        switch (level.toLowerCase(Locale.ROOT)) {
            case "very low":
                return DefenceLevel.VERY_LOW;
            case "low":
                return DefenceLevel.LOW;
            case "medium":
                return DefenceLevel.MEDIUM;
            case "high":
                return DefenceLevel.HIGH;
            case "very high":
                return DefenceLevel.VERY_HIGH;
            default:
                WynnUtilityMod.LOGGER.info("Defence" + level + " not found!");
                return null;
        }
    }

    private final Color displayColor;
    private final String displayText;

    DefenceLevel(Color displayColor, String displayText) {
        this.displayColor = displayColor;
        this.displayText = displayText;
    }

    public Color getDisplayColor() {
        return this.displayColor;
    }

    public String getDisplayText() {
        return this.displayText;
    }
}
