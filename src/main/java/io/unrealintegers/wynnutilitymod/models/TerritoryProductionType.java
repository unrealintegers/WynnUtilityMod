package io.unrealintegers.wynnutilitymod.models;

import org.jetbrains.annotations.Nullable;

import java.awt.*;


public enum TerritoryProductionType {
    ORE('\u24b7', new Color(0xaaaaaa)),
    WOOD('\u24b8', new Color(0xffaa00)),
    FISH('\u24c0', new Color(0x55ffff)),
    CROPS('\u24bf', new Color(0xffff55)),
    OASIS('\u2b1f', new Color(0xff55ff));

    public static TerritoryProductionType fromResourceType(ResourceType resourceType) {
        try {
            return valueOf(resourceType.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public final Character Symbol;
    public final Color Color;

    TerritoryProductionType(@Nullable Character symbol, Color color) {
        this.Symbol = symbol;
        this.Color = color;
    }
}
