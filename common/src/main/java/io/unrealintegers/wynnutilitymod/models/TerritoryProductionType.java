package io.unrealintegers.wynnutilitymod.models;

import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;


public enum TerritoryProductionType {
    ORE('\u24b7', Formatting.GRAY),
    WOOD('\u24b8', Formatting.GOLD),
    FISH('\u24c0', Formatting.AQUA),
    CROPS('\u24bf', Formatting.YELLOW),
    OASIS('\u2b1f', Formatting.LIGHT_PURPLE);

    public static TerritoryProductionType fromResourceType(ResourceType resourceType) {
        try {
            return valueOf(resourceType.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public final Character symbol;
    private final Formatting format;

    TerritoryProductionType(@Nullable Character symbol, Formatting format) {
        this.symbol = symbol;
        this.format = format;
    }

    public String getFormat() {
        return format.toString();
    }
}
