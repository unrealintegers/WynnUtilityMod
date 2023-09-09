package io.unrealintegers.wynnutilitymod.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.util.ActionResult;

public interface BossBarCallback {
    Event<BossBarCallback> EVENT = EventFactory.createArrayBacked(BossBarCallback.class,
            (listeners) -> (event) -> {
                for (BossBarCallback listener : listeners) {
                    ActionResult result = listener.interact(event);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(BossBarS2CPacket packet);
}

