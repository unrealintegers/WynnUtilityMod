package io.unrealintegers.wynnutilitymod.mixin;

import io.unrealintegers.wynnutilitymod.keyinding.KeybindManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenKeyMixin {
    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        KeybindManager.processGuiKeys(keyCode, scanCode, modifiers, (Screen) (Object) this);
    }
}
