package io.unrealintegers.wynnutilitymod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.HashMap;
import java.util.Map;

public class KeybindManager {
    private KeybindManager() {
    }

    private static final Map<KeyBinding, Runnable> keybinds = new HashMap<>();

    public static void init() {
    }

    private static void register(String name, int defaultKey, String category, KeyConflictContext conflictContext,
                                 Runnable callback) {
        KeyBinding key = new KeyBinding(name, defaultKey, category);
        ClientRegistry.registerKeyBinding(key);
        keybinds.put(key, callback);
    }

    public static void handleKeys() {
        keybinds.forEach((key, callback) -> {
            if (key.isPressed()) {
                callback.run();
            }
        });
    }

}
