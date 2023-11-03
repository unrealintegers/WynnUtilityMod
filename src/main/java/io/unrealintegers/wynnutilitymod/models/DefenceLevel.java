package io.unrealintegers.wynnutilitymod.models;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Locale;

public enum DefenceLevel {
    VERY_LOW(Formatting.AQUA, "-"),
    LOW(Formatting.GREEN, "L"),
    MEDIUM(Formatting.YELLOW, "M"),
    HIGH(Formatting.RED, "H"),
    VERY_HIGH(Formatting.DARK_PURPLE, "V");

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

    private final Formatting displayFormat;
    private final String displayText;

    DefenceLevel(Formatting displayFormat, String displayText) {
        this.displayFormat = displayFormat;
        this.displayText = displayText;
    }

    public String getFormat() {
        return this.displayFormat.toString();
    }

    public String getDisplayText() {
        return this.displayText;
    }
}
