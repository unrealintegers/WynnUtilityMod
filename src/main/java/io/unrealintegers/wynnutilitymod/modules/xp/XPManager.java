package io.unrealintegers.wynnutilitymod.modules.xp;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.scheduler.Scheduler;
import io.unrealintegers.wynnutilitymod.util.NumberFormatter;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class XPManager {
    static {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("gxp")
                        .then(argument("action", string())
                                .executes(ctx -> {
                                    String action = getString(ctx, "action").toLowerCase(Locale.ROOT);

                                    if (action.equals("reload")) {
                                        WynnUtilityMod.getXpManager().init();
                                        return 1;
                                    }

                                    if (!WynnUtilityMod.getXpManager().hasGuild) {
                                        ctx.getSource().sendFeedback(Text.literal("You are not in a guild!"));
                                        return 1;
                                    }
                                    switch (action) {
                                        case "enable" -> {
                                            WynnUtilityMod.getXpManager().state = true;
                                            ctx.getSource().sendFeedback(Text.literal("XP Tracking Started."));
                                        }
                                        case "disable" -> {
                                            WynnUtilityMod.getXpManager().state = false;
                                            ctx.getSource().sendFeedback(Text.literal("XP Tracking Stopped."));
                                        }
                                        case "on", "show" -> {
                                            WynnUtilityMod.getXpManager().visibility = true;
                                            ctx.getSource().sendFeedback(Text.literal("XP Tracking Visibility: Shown."));
                                        }
                                        case "off", "hide" -> {
                                            WynnUtilityMod.getXpManager().visibility = false;
                                            ctx.getSource().sendFeedback(Text.literal("XP Tracking Visibility: Hidden."));
                                        }
                                        case "debug" -> {
                                            XPManager instance = WynnUtilityMod.getXpManager();
                                            ctx.getSource().sendFeedback(Text.literal(instance.xpStates.toString()));
                                        }
                                    }
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            if (!WynnUtilityMod.getXpManager().hasGuild) {
                                ctx.getSource().sendFeedback(Text.literal("You are not in a guild!"));
                                return 1;
                            }

                            WynnUtilityMod.getXpManager().visibility = !WynnUtilityMod.getXpManager().visibility;

                            if (WynnUtilityMod.getXpManager().visibility)
                                ctx.getSource().sendFeedback(Text.literal("XP Tracking Visibility: Shown."));
                            else
                                ctx.getSource().sendFeedback(Text.literal("XP Tracking Visibility: Hidden."));

                            return 1;
                        })
        ));
        HudRenderCallback.EVENT.register((mStack, tickDelta) -> {
            WynnUtilityMod.getXpManager().render(mStack);
        });
    }

    private Long avg1m, avg5m, avg15m;
    private final Map<Long, Long> xpStates = new TreeMap<>(Collections.reverseOrder());
    private boolean hasGuild = false;
    public boolean state;
    public boolean visibility;
    private UpdateXPTask task = null;

    public XPManager() {
        this.state = true;
        this.visibility = false;
    }

    public void init() {
        try {
            if (this.task != null) {
                Scheduler.cancelTask(this.task);
            }
            this.task = new UpdateXPTask(this);
            Scheduler.addTask(this.task);
            hasGuild = true;
        } catch (IOException | InterruptedException | IllegalStateException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            hasGuild = false;
        }
    }

    void render(MatrixStack mStack) {
        if (!state || !visibility || !hasGuild) return;
        
        int x = 2;
        int y = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2;

        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        int ex = tr.fontHeight;
        
        tr.drawWithShadow(mStack, "Guild XP", x, y, Color.white.getRGB());

        String xp1m = avg1m == null ? "  1m: ---" : String.format("  1m: %s/h", NumberFormatter.format(avg1m));
        tr.drawWithShadow(mStack, xp1m, x, y + ex, Color.white.getRGB());

        String xp5m = avg5m == null ? "  5m: ---" : String.format("  5m: %s/h", NumberFormatter.format(avg5m));
        tr.drawWithShadow(mStack, xp5m, x, y + 2 * ex, Color.white.getRGB());

        String xp15m = avg15m == null ? "  15m: ---" : String.format("  15m: %s/h", NumberFormatter.format(avg15m));
        tr.drawWithShadow(mStack, xp15m, x, y + 3 * ex, Color.white.getRGB());
    }

    void updateXP(long xp) {
        if (!state) return;

        long time = System.currentTimeMillis();
        // 59 instead of 60 to allow for API request time
        avg1m = xpStates.entrySet().stream().filter(e -> time - e.getKey() >= 59 * 1000)
                .findFirst().map(e -> (xp - e.getValue()) * 3600 * 1000 / (time - e.getKey())).orElse(null);
        avg5m = xpStates.entrySet().stream().filter(e -> time - e.getKey() >= 5 * 59 * 1000)
                .findFirst().map(e -> (xp - e.getValue()) * 3600 * 1000 / (time - e.getKey())).orElse(null);
        avg15m = xpStates.entrySet().stream().filter(e -> time - e.getKey() >= 15 * 59 * 1000)
                .findFirst().map(e -> (xp - e.getValue()) * 3600 * 1000 / (time - e.getKey())).orElse(null);

        xpStates.entrySet().removeIf(e -> e.getKey() < time - 17 * 60 * 1000);
        xpStates.put(time, xp);
    }
}
