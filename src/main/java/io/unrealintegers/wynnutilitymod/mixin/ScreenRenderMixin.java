package io.unrealintegers.wynnutilitymod.mixin;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.modules.eco.EcoOverlayManager;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public abstract class ScreenRenderMixin {
    @Inject(method = "render",
    at = @At("HEAD"))
    private void preRender(MatrixStack mStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (EcoOverlayManager.guiCheck((GenericContainerScreen) (Object) this)) {
            WynnUtilityMod.getEcoOverlayManager().preRender(mStack, (GenericContainerScreen) (Object) this);
        }
    }

    @Inject(method = "render",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/GenericContainerScreen;drawMouseoverTooltip(Lnet/minecraft/client/util/math/MatrixStack;II)V"))
    private void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (EcoOverlayManager.guiCheck((GenericContainerScreen) (Object) this)) {
            WynnUtilityMod.getEcoOverlayManager().render(mStack, (GenericContainerScreen) (Object) this);
        }
    }
}
