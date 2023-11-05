package io.unrealintegers.wynnutilitymod.fabric;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import net.fabricmc.api.ClientModInitializer;

public class WynnUtilityModFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WynnUtilityMod.init();
    }
}
