package io.unrealintegers.wynnutilitymod.model;

import net.minecraft.client.Minecraft;

import java.util.UUID;

public class Player {
    public static String name = Minecraft.getMinecraft().getSession().getUsername();
    public static UUID uuid = Minecraft.getMinecraft().getSession().getProfile().getId();
    public static String guildName;
}
