package io.unrealintegers.wynnutilitymod.models;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;

import java.awt.*;
import java.util.Locale;

public enum TreasuryLevel {
    VERY_LOW(0, new Color(191, 0, 0), 0),
    LOW(1, new Color(191, 95, 0), 1000 * 3600),
    MEDIUM(2, new Color(191, 191, 0), 1000 * 3600 * 24),
    HIGH(2.5f, new Color(0, 191, 0), 1000 * 3600 * 24 * 5),
    VERY_HIGH(3, new Color(0, 191, 191), 1000 * 3600 * 24 * 12);

    public static TreasuryLevel parse(String level) {
        switch (level.toLowerCase(Locale.ROOT)) {
            case "very low":
                return TreasuryLevel.VERY_LOW;
            case "low":
                return TreasuryLevel.LOW;
            case "medium":
                return TreasuryLevel.MEDIUM;
            case "high":
                return TreasuryLevel.HIGH;
            case "very high":
                return TreasuryLevel.VERY_HIGH;
            default:
                WynnUtilityMod.LOGGER.warn("Treasury " + level + " not found!");
                return null;
        }
    }

    private static final TreasuryLevel[] vals = TreasuryLevel.values();

    private final float bonusMultiplier;
    private final Color displayColor;
    private final long heldTime;

    TreasuryLevel(float bonusMultiplier, Color displayColor, long heldTime) {
        this.bonusMultiplier = bonusMultiplier;
        this.displayColor = displayColor;
        this.heldTime = heldTime;
    }

    public float getBonusMultiplier() {
        return this.bonusMultiplier;
    }

    public Color getDisplayColor() {
        return this.displayColor;
    }

    public double getProgress(long heldTime) {
        long timeNeeded = nextTier().heldTime - this.heldTime;

        if (timeNeeded == 0) {
            return 1d;
        }

        return (double) (heldTime - this.heldTime) / timeNeeded;
    }

    public TreasuryLevel nextTier() {
        return vals[Math.min(this.ordinal() + 1, vals.length - 1)];
    }
}
