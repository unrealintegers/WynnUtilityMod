package io.unrealintegers.wynnutilitymod.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

public interface BossBarCallback {
    Event<BossBarCallback> EVENT = EventFactory.createEventResult();

    void onBossBarPacket(BossBarS2CPacket packet);
}



