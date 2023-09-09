package io.unrealintegers.wynnutilitymod.models;

import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;

public class TerritoryUpgrades {
    private static class Bonuses {
        private static final float[] ATTACK_HEALTH = {0f, 0.5f, 1f, 1.5f, 2.2f, 3f, 4f, 5f, 6.2f, 6.6f, 7.4f, 8.4f};
        private static final float[] DEFENCE = {0.1f, 0.4f, 0.55f, 0.625f, 0.68f, 0.72f, 0.745f, 0.765f, 0.78f, 0.795f, 0.81f, 0.82f};
        private static final int[] AURA = {0, 24, 18, 12};
        private static final int[] VOLLEY = {0, 20, 15, 10};
        private static final float[] EXPERIENCE = {0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.8f, 1f};
        private static final float[] MOB_DAMAGE = {0f, 0.1f, 0.2f, 0.4f, 0.6f, 0.8f, 1.2f, 1.6f, 2f};
        private static final float[] PVP_DAMAGE = {0f, 0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.4f, 0.65f, 0.8f};
        private static final int[] XP_SEEKING = {0, 36000, 66000, 120000, 228000, 456000, 900000, 1740000, 2580000, 3360000};
        private static final float[] EMERALD_SEEKING = {0f, 0.003f, 0.03f, 0.06f, 0.12f, 0.24f};
        private static final int[] STORAGE = {1, 2, 4, 8, 15, 34, 80};
        private static final float[] EFFICIENT_EMERALDS = {1f, 1.35f, 2f, 4f};
    }

    protected static float getResourceEfficiency(int level) {
        return 1f + 0.5f * level;
    }

    protected static float getEmeraldEfficiency(int level) {
        return Bonuses.EFFICIENT_EMERALDS[level];
    }

    protected static int getResourceRate(int level) {
        return 4 - level;
    }

    protected static int getEmeraldRate(int level) {
        return 4 - level;
    }

    protected static float getResourceMultiplier(int efficiency, int rate) {
        return 4 * getResourceEfficiency(efficiency) / getResourceRate(rate);
    }

    protected static float getEmeraldMultiplier(int efficiency, int rate) {
        return 4 * getEmeraldEfficiency(efficiency) / getEmeraldRate(rate);
    }

    private final EnumMap<TerritoryUpgradeType, Integer> levels = new EnumMap<>(TerritoryUpgradeType.class);

    public TerritoryUpgrades() {
    }

    public Integer get(TerritoryUpgradeType type) {
        return levels.get(type);
    }

    public Integer get(TerritoryUpgradeType type, int defaultValue) {
        Integer value = levels.get(type);
        if (value == null) value = defaultValue;
        return value;
    }

    public Integer getCost(TerritoryUpgradeType type) {
        Integer level = levels.get(type);
        if (level == null) {
            return null;
        } else {
            return type.getCost(level);
        }
    }

    public void set(TerritoryUpgradeType type, int level) {
        if (level >= 0 && level <= type.getMaxLevel()) {
            levels.put(type, level);
        } else {
            throw new IndexOutOfBoundsException("Invalid Level");
        }
    }

    public void reset() {
        levels.clear();
    }

    public int getEmeraldCost() {
        Optional<Integer> result = TerritoryUpgradeType.EMERALD_UPGRADES.stream().map(this::getCost).filter(Objects::nonNull).reduce(Integer::sum);
        return result.orElse(0);
    }

    public int getOreCost() {
        Optional<Integer> result = TerritoryUpgradeType.ORE_UPGRADES.stream().map(this::getCost).filter(Objects::nonNull).reduce(Integer::sum);
        return result.orElse(0);
    }

    public int getWoodCost() {
        Optional<Integer> result = TerritoryUpgradeType.WOOD_UPGRADES.stream().map(this::getCost).filter(Objects::nonNull).reduce(Integer::sum);
        return result.orElse(0);
    }

    public int getFishCost() {
        Optional<Integer> result = TerritoryUpgradeType.FISH_UPGRADES.stream().map(this::getCost).filter(Objects::nonNull).reduce(Integer::sum);
        return result.orElse(0);
    }

    public int getCropCost() {
        Optional<Integer> result = TerritoryUpgradeType.CROP_UPGRADES.stream().map(this::getCost).filter(Objects::nonNull).reduce(Integer::sum);
        return result.orElse(0);
    }
}
