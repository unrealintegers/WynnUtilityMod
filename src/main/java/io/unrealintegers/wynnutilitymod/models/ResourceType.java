package io.unrealintegers.wynnutilitymod.models;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;

public enum ResourceType {
    EMERALDS(null, new Color(0x55ff55)),
    ORE('\u24b7', new Color(65, 82, 82)),
    WOOD('\u24b8', new Color(213, 157, 42)),
    FISH('\u24c0', new Color(87, 234, 234)),
    CROPS('\u24bf', new Color(227, 227, 72));

    public static final ResourceType[] NON_EMERALD_TYPES = Arrays.copyOfRange(values(), 1, 5);

    public static ResourceType fromProductionType(TerritoryProductionType productionType) {
        try {
            return valueOf(productionType.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public final Character Symbol;
    public final Color Color;

    ResourceType(@Nullable Character symbol, Color color) {
        this.Symbol = symbol;
        this.Color = color;
    }
}
