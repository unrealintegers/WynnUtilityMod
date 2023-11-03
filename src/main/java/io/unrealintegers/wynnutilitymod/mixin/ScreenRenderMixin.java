package io.unrealintegers.wynnutilitymod.mixin;

import io.unrealintegers.wynnutilitymod.modules.eco.EcoOverlayScreen;
import io.unrealintegers.wynnutilitymod.modules.eco.EcoOverlayScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public abstract class ScreenRenderMixin {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
    at = @At("HEAD"))
    private void preRender(DrawContext context, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GenericContainerScreen thisScreen = (GenericContainerScreen) (Object) this;
        PlayerInventory playerInventory = MinecraftClient.getInstance().player.getInventory();
        if (EcoOverlayScreen.guiCheck(thisScreen)) {
            MinecraftClient.getInstance().setScreen(new EcoOverlayScreen(
                    new EcoOverlayScreenHandler(null,
                            thisScreen.getScreenHandler().syncId,
                            playerInventory,
                            thisScreen.getScreenHandler().getInventory()), playerInventory));
        }
    }
}
