package io.unrealintegers.wynnutilitymod.modules.war;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.event.BossBarCallback;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BossBarListener {
    private static final Pattern TOWER_PATTERN = Pattern.compile(
            "§3\\[(?<tag>[A-Za-z]{3,4})] §b(?<territory>[A-Za-z \\-']+) Tower§7 - " +
                    "§4❤ (?<hp>\\d+)§7 \\(§6(?<def>[\\d.]+)%§7\\) - " +
                    "§c☠ (?<dmin>\\d+)-(?<dmax>\\d+)§7 \\(§b(?<as>[\\d.]+)x§7\\)"
    );
    private static final Set<String> found = new HashSet<>();

    public BossBarListener() {
        BossBarCallback.EVENT.register(this::onBossBarPacket);
    }

    public ActionResult onBossBarPacket(@NotNull BossBarS2CPacket packet) {
        packet.accept(
                new BossBarS2CPacket.Consumer() {
                    private Text processName(Text name) {
                        Matcher matcher = TOWER_PATTERN.matcher(name.getString());

                        if (!matcher.find()) {
                            return null;
                        }

                        String tag = matcher.group("tag");
                        String territory = matcher.group("territory");
                        long hp = Long.parseLong(matcher.group("hp"));
                        double def = Double.parseDouble(matcher.group("def"));
                        int dmin = Integer.parseInt(matcher.group("dmin"));
                        int dmax = Integer.parseInt(matcher.group("dmax"));
                        float as = Float.parseFloat(matcher.group("as"));
                        String updated = WynnUtilityMod.getTowerManager().update(tag, territory, hp, def, dmin, dmax, as);

                        return Text.of(updated);
                    }

                    @Override
                    public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style,
                                    boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
                        Text updatedText = processName(name);
                        if (updatedText == null) return;

                        try {
                            FieldUtils.writeField(FieldUtils.readField(packet, "action", true),
                                    "name", updatedText, true);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void updateName(UUID uuid, Text name) {
                        Text updatedText = processName(name);
                        if (updatedText == null) return;

                        try {
                            FieldUtils.writeField(FieldUtils.readField(packet, "action", true),
                                    "name", updatedText, true);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        return ActionResult.PASS;
    }
}
