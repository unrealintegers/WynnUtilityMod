package io.unrealintegers.wynnutilitymod;

import io.unrealintegers.wynnutilitymod.modules.eco.EcoOverlayScreen;
import io.unrealintegers.wynnutilitymod.keyinding.KeybindManager;
import io.unrealintegers.wynnutilitymod.models.Wynncraft;
import io.unrealintegers.wynnutilitymod.modules.xp.XPManager;
import io.unrealintegers.wynnutilitymod.scheduler.Scheduler;
import io.unrealintegers.wynnutilitymod.modules.war.TowerManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceType;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WynnUtilityMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("wum");
    private static final TowerManager towerManager = new TowerManager();
    private static final XPManager xpManager = new XPManager();

    public static TowerManager getTowerManager() {
        return towerManager;
    }

    public static XPManager getXpManager() {
        return xpManager;
    }

    @Override
    public void onInitializeClient() {
        xpManager.init();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(Wynncraft.listener);

        KeybindManager.registerGui("key.cycle_mode", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT, "key.category.eco", (screen) -> {
                    if (screen instanceof EcoOverlayScreen ecoOverlayScreen) {
                        ecoOverlayScreen.cycle();
                        LOGGER.info("Cycling eco mode");
                    }
                });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            Scheduler.tick();
        });
    }
}
