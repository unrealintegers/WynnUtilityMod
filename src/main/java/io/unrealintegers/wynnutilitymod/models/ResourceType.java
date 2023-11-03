package io.unrealintegers.wynnutilitymod.models;

import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;

public enum ResourceType {
    EMERALDS(null, Formatting.GREEN),
    ORE('\u24b7', Formatting.GRAY),
    WOOD('\u24b8', Formatting.GOLD),
    FISH('\u24c0', Formatting.AQUA),
    CROPS('\u24bf', Formatting.YELLOW);

    public static final ResourceType[] NON_EMERALD_TYPES = Arrays.copyOfRange(values(), 1, 5);

    public static ResourceType fromProductionType(TerritoryProductionType productionType) {
        try {
            return valueOf(productionType.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public final Character symbol;
    private final Formatting format;

    ResourceType(@Nullable Character symbol, Formatting format) {
        this.symbol = symbol;
        this.format = format;
    }

    public String getFormat() {
        return format.toString();
    }
}
