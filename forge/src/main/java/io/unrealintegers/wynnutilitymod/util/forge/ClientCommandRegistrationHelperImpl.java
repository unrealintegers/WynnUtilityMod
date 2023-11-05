package io.unrealintegers.wynnutilitymod.util.forge;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientCommandRegistrationHelperImpl {
    private static List<LiteralArgumentBuilder<?>> builders = new ArrayList<>();

    public static void init() {
        MinecraftForge.EVENT_BUS.register(ClientCommandRegistrationHelperImpl.class);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        builders.forEach(builder -> event.getDispatcher().register((LiteralArgumentBuilder<ServerCommandSource>) builder));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builders.add(builder);
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> ctx, Text message) {
        ctx.getSource().sendFeedback(() -> message, false);
    }
}
