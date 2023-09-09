package io.unrealintegers.betterwynnmap.model;

import io.unrealintegers.wynnutilitymod.models.Territory;

public class Guild {
    public final String name;
    public final String tag;

    public Territory HQ;

    public Guild(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }
}
