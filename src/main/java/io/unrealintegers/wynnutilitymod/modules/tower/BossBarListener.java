package io.unrealintegers.wynnutilitymod.modules.tower;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.unrealintegers.wynnutilitymod.WUMMod;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ChannelHandler.Sharable
public class BossBarListener extends ChannelInboundHandlerAdapter {
    private static final Pattern TOWER_PATTERN = Pattern.compile("§3\\[(?<tag>[A-Za-z]{3,4})] §b(?<name>[A-Za-z \\-']+) Tower§7 - " +
            "§4❤ (?<hp>\\d+)§7 \\(§6(?<def>[\\d.]+)%§7\\) - §c☠ (?<dmin>\\d+)-(?<dmax>\\d+)§7 \\(§b(?<as>[\\d.]+)x§7\\)");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof SPacketUpdateBossInfo) {
                SPacketUpdateBossInfo bossBarInfo = (SPacketUpdateBossInfo) msg;

                if (bossBarInfo.getName() == null) {
                    super.channelRead(ctx, msg);
                    return;
                }

                Matcher matcher = TOWER_PATTERN.matcher(bossBarInfo.getName().getUnformattedText());

                if (!matcher.find()) {
                    super.channelRead(ctx, msg);
                    return;
                }

                String tag = matcher.group("tag");
                String name = matcher.group("name");
                long hp = Long.parseLong(matcher.group("hp"));
                double def = Double.parseDouble(matcher.group("def"));
                int dmin = Integer.parseInt(matcher.group("dmin"));
                int dmax = Integer.parseInt(matcher.group("dmax"));
                float as = Float.parseFloat(matcher.group("as"));
                String updated = WUMMod.getTowerManager().update(tag, name, hp, def, dmin, dmax, as);

                FieldUtils.writeField(bossBarInfo, "field_186913_c", new TextComponentString(updated), true);
            }


            super.channelRead(ctx, msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
