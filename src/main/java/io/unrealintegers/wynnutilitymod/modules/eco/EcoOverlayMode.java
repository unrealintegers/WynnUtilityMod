package io.unrealintegers.wynnutilitymod.modules.eco;

public enum EcoOverlayMode {
    PRODUCTIONS,
    TOWER_LEVELS;

    private static final EcoOverlayMode[] values = values();

    public EcoOverlayMode next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
