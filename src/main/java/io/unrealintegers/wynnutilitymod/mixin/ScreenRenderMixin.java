package io.unrealintegers.wynnutilitymod.mixin;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.modules.eco.EcoOverlayManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public abstract class ScreenRenderMixin {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
    at = @At("HEAD"))
    private void preRender(DrawContext context, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (EcoOverlayManager.guiCheck((GenericContainerScreen) (Object) this)) {
            WynnUtilityMod.getEcoOverlayManager().preRender(context, (GenericContainerScreen) (Object) this);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/GenericContainerScreen;drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V"))
    private void render(DrawContext context, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (EcoOverlayManager.guiCheck((GenericContainerScreen) (Object) this)) {
            WynnUtilityMod.getEcoOverlayManager().render(context, (GenericContainerScreen) (Object) this);
        }
    }
}
