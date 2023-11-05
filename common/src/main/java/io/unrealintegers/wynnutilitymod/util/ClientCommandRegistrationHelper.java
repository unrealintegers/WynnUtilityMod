package io.unrealintegers.wynnutilitymod.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.ArrayList;
import java.util.List;

public class ClientCommandRegistrationHelper {
    private static List<LiteralArgumentBuilder<?>> builders = new ArrayList<>();

    static {
        init();
    }

    @ExpectPlatform
    private static void init() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void register(LiteralArgumentBuilder<?> builder) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendFeedback(CommandContext ctx, Text message) {
        throw new AssertionError();
    }
}
