package io.unrealintegers.wynnutilitymod.models;

public class ResourceInfo {
    private int currentLevel;
    private int maxLevel;
    private final int baseProduction;
    private int currentProduction;


    public ResourceInfo(int baseProduction) {
        this(0, 0, baseProduction, 0);
    }

    public ResourceInfo(int currentLevel, int maxLevel, int baseProduction, int currentProduction) {
        this.currentLevel = currentLevel;
        this.maxLevel = maxLevel;
        this.baseProduction = baseProduction;
        this.currentProduction = currentProduction;
    }

    public int getCurrentStored() {
        return this.currentLevel;
    }

    public int getMaxStored() {
        return this.maxLevel;
    }

    public int getBaseProduction() {
        return this.baseProduction;
    }

    public int getCurrentProduction() {
        return this.currentProduction;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setCurrentProduction(int production) {
        this.currentProduction = production;
    }

    public Float getMultiplier() {
        if (baseProduction == 0) {
            return null;
        } else {
            return (float) currentProduction / baseProduction;
        }
    }
}
