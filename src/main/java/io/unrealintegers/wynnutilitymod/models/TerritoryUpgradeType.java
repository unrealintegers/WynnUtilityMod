package io.unrealintegers.wynnutilitymod.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TerritoryUpgradeType {
    TOWER_DAMAGE(ResourceType.ORE, "Damage",
            0, 100, 300, 600, 1200, 2400, 4800, 8400, 12000, 15600, 19200, 22800),
    TOWER_ATTACK(ResourceType.CROPS, "Attack",
            0, 100, 300, 600, 1200, 2400, 4800, 8400, 12000, 15600, 19200, 22800),
    TOWER_HEALTH(ResourceType.WOOD, "Health",
            0, 100, 300, 600, 1200, 2400, 4800, 8400, 12000, 15600, 19200, 22800),
    TOWER_DEFENCE(ResourceType.FISH, "Defence",
            0, 100, 300, 600, 1200, 2400, 4800, 8400, 12000, 15600, 19200, 22800),
    STRONGER_MINIONS(ResourceType.WOOD, "Stronger Minions",
            0, 200, 400, 800, 1600),
    TOWER_MULTI_ATTACKS(ResourceType.FISH, "Tower Multi-Attacks",
            0, 4800, 9600),
    TOWER_AURA(ResourceType.CROPS, "Tower Aura",
            0, 800, 1600, 3200),
    TOWER_VOLLEY(ResourceType.ORE, "Tower Volley",
            0, 200, 400, 800),
    GATHERING_EXPERIENCE(ResourceType.WOOD, "Gathering Experience",
            0, 600, 1300, 2000, 2700, 3400, 5500, 10000, 20000),
    MOB_EXPERIENCE(ResourceType.FISH, "Mob Experience",
            0, 600, 1200, 1800, 2400, 3000, 5000, 10000, 20000),
    MOB_DAMAGE(ResourceType.CROPS, "Mob Damage",
            0, 600, 1200, 1800, 2400, 3000, 5000, 10000, 20000),
    PVP_DAMAGE(ResourceType.ORE, "PvP Damage",
            0, 600, 1200, 1800, 2400, 3000, 5000, 10000, 20000),
    XP_SEEKING(ResourceType.EMERALDS, "XP Seeking",
            0, 100, 200, 400, 800, 1600, 3200, 6400, 9600, 12800),
    TOME_SEEKING(ResourceType.FISH, "Tome Seeking",
            0, 400, 3200, 6400),
    EMERALD_SEEKING(ResourceType.WOOD, "Emerald Seeking",
            0, 200, 800, 1600, 3200, 6400),
    LARGER_RESOURCE_STORAGE(ResourceType.EMERALDS, "Larger Resource Storage",
            0, 400, 800, 2000, 5000, 16000, 48000),
    LARGER_EMERALD_STORAGE(ResourceType.WOOD,  "Larger Emerald Storage",
            0, 200, 400, 1000, 2500, 8000, 24000),
    EFFICIENT_RESOURCES(ResourceType.EMERALDS, "Efficient Resources",
            0, 6000, 12000, 24000, 48000, 96000, 192000),
    EFFICIENT_EMERALDS(ResourceType.ORE, "Efficient Emeralds",
            0, 2000, 8000, 32000),
    RESOURCE_RATE(ResourceType.EMERALDS, "Resource Rate",
            0, 6000, 18000, 32000),
    EMERALD_RATE(ResourceType.CROPS, "Emerald Rate",
            0, 2000, 8000, 32000);

    static final Map<String, TerritoryUpgradeType> GAME_NAMES = new HashMap<>();

    static final List<TerritoryUpgradeType> EMERALD_UPGRADES = new ArrayList<>();
    static final List<TerritoryUpgradeType> ORE_UPGRADES = new ArrayList<>();
    static final List<TerritoryUpgradeType> WOOD_UPGRADES = new ArrayList<>();
    static final List<TerritoryUpgradeType> FISH_UPGRADES = new ArrayList<>();
    static final List<TerritoryUpgradeType> CROP_UPGRADES = new ArrayList<>();

    static {
        for (TerritoryUpgradeType type : TerritoryUpgradeType.values()) {
            // Populate gameName map
            GAME_NAMES.put(type.gameName, type);

            // Populate Type Lists
            switch (type.costType) {
                case EMERALDS:
                    EMERALD_UPGRADES.add(type);
                    break;
                case ORE:
                    ORE_UPGRADES.add(type);
                    break;
                case WOOD:
                    WOOD_UPGRADES.add(type);
                    break;
                case FISH:
                    FISH_UPGRADES.add(type);
                    break;
                case CROPS:
                    CROP_UPGRADES.add(type);
                    break;
            }
        }
    }

    static TerritoryUpgradeType fromGameName(String gameName) {
        return GAME_NAMES.get(gameName);
    }

    private final ResourceType costType;
    private final String gameName;
    private final int[] costs;

    TerritoryUpgradeType(ResourceType type, String gameName, int... costs) {
        this.costType = type;
        this.gameName = gameName;
        this.costs = costs;
    }

    public ResourceType getType() {
        return this.costType;
    }

    public int getCost(int level) {
        return this.costs[level];
    }

    public int getMaxLevel() {
        return costs.length - 1;
    }
}
