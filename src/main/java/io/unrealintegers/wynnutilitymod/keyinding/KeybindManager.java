package io.unrealintegers.wynnutilitymod.keyinding;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KeybindManager {
    public static Map<KeyBinding, Runnable> keybinds = new HashMap<>();
    public static Map<KeyBinding, Consumer<Screen>> guiKeybinds = new HashMap<>();

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (KeyBinding keybind : keybinds.keySet()) {
                if (keybind.wasPressed()) {
                    keybinds.get(keybind).run();
                }
            }
        });
    }

    public static void register(String identifier, InputUtil.Type type, int defaultKey,
                                String category, Runnable callback) {
        KeyBinding keybind = new KeyBinding(identifier, type, defaultKey, category);
        KeyBindingHelper.registerKeyBinding(keybind);
        keybinds.put(keybind, callback);
    }

    public static void registerGui(String identifier, InputUtil.Type type, int defaultKey,
                                   String category, Consumer<Screen> callback) {
        KeyBinding keybind = new KeyBinding(identifier, type, defaultKey, category);
        KeyBindingHelper.registerKeyBinding(keybind);
        guiKeybinds.put(keybind, callback);
    }

    public static void processGuiKeys(int keyCode, int scanCode, int modifiers, Screen screen) {
        for (KeyBinding keybind : guiKeybinds.keySet()) {
            if (keybind.matchesKey(keyCode, scanCode)) {
                guiKeybinds.get(keybind).accept(screen);
            }
        }
    }
}
