package io.unrealintegers.wynnutilitymod.forge;

import dev.architectury.platform.forge.EventBuses;
import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WynnUtilityMod.MOD_ID)
public class WynnUtilityModForge {
    public WynnUtilityModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(WynnUtilityMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        WynnUtilityMod.init();
    }
}
