package io.unrealintegers.wynnutilitymod.util.fabric;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.platform.Platform;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClientCommandRegistrationHelperImpl {
    private static List<LiteralArgumentBuilder<?>> builders = new ArrayList<>();

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> builders.forEach(builder ->
                dispatcher.register((LiteralArgumentBuilder<FabricClientCommandSource>) builder))));
    }

    public static void register(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        builders.add(builder);
    }

    public static void sendFeedback(CommandContext<FabricClientCommandSource> ctx, Text message) {
        ctx.getSource().sendFeedback(message);
    }
}
