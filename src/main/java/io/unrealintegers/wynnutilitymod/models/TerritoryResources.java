package io.unrealintegers.wynnutilitymod.models;

import java.util.EnumMap;

public class TerritoryResources {
    private final EnumMap<ResourceType, ResourceInfo> resources;

    public TerritoryResources(EnumMap<ResourceType, Integer> baseProductions) {
        this.resources = new EnumMap<>(ResourceType.class);

        for (ResourceType type : ResourceType.values()) {
            resources.put(type, new ResourceInfo(baseProductions.get(type)));
        }
    }

    public ResourceInfo getResource(ResourceType type) {
        return resources.get(type);
    }
}
