package io.unrealintegers.wynnutilitymod.mixin;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.event.BossBarCallback;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public abstract class BossBarMixin {
    @Inject(method = "handlePacket(Lnet/minecraft/network/packet/s2c/play/BossBarS2CPacket;)V",
            at = @At("HEAD")
    )
    private void onPacket(BossBarS2CPacket packet, CallbackInfo ci) {
        BossBarCallback.EVENT.invoker().interact(packet);
    }
}
