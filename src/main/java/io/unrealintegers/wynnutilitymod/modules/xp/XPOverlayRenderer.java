package io.unrealintegers.wynnutilitymod.modules.xp;


import io.unrealintegers.wynnutilitymod.WUMMod;
import io.unrealintegers.wynnutilitymod.utils.NumberFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WUMMod.MODID)
public class XPOverlayRenderer {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void render(RenderGameOverlayEvent.Post event) {
        int x = 2;
        int y = Minecraft.getMinecraft().displayHeight / Minecraft.getMinecraft().gameSettings.guiScale / 2;

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        int ex = fontRenderer.FONT_HEIGHT;
        Long avg1m = WUMMod.getXpManager().getAvgXP1m();
        Long avg5m = WUMMod.getXpManager().getAvgXP5m();
        Long avg15m = WUMMod.getXpManager().getAvgXP15m();

        if (avg1m != null && avg1m > 0) {
            String xp1 = String.format("1m: %s/h", NumberFormatter.format(avg1m));
            fontRenderer.drawString(xp1, x, y, 0xffffffff);
        }
        if (avg5m != null && avg5m > 0) {
            String xp1 = String.format("5m: %s/h", NumberFormatter.format(avg5m));
            fontRenderer.drawString(xp1, x, y + ex, 0xffffffff);
        }
        if (avg15m != null && avg15m > 0) {
            String xp1 = String.format("15m: %s/h", NumberFormatter.format(avg15m));
            fontRenderer.drawString(xp1, x, y + 2 * ex, 0xffffffff);
        }
    }
}