package io.unrealintegers.wynnutilitymod;

import io.netty.channel.ChannelPipeline;
import io.unrealintegers.wynnutilitymod.model.Player;
import io.unrealintegers.wynnutilitymod.modules.tower.BossBarListener;
import io.unrealintegers.wynnutilitymod.modules.xp.UpdateXPTask;
import io.unrealintegers.wynnutilitymod.scheduler.Scheduler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WUMMod.MODID)
public class ClientEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTick(TickEvent.ClientTickEvent event) {
        KeybindManager.handleKeys();
        Scheduler.tick();
        WUMMod.getTowerManager().tick();
    }

    @SubscribeEvent
    public static void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        ChannelPipeline pipeline = event.getManager().channel().pipeline();
        pipeline.addBefore("packet_handler", BossBarListener.class.getName(), new BossBarListener());

        if (Player.guildName != null) {
            Scheduler.addTask(new UpdateXPTask(WUMMod.getXpManager()));
        }
    }

    @SubscribeEvent
    public static void onServerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Scheduler.reset();
    }
}
