package io.unrealintegers.wynnutilitymod.models;

import io.unrealintegers.wynnutilitymod.util.NumberFormatter;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public class Tower {
    private final String name;
    private final String ownerTag;
    private long currentHP;
    private final long maxHP;
    private final double defPercent;
    private int minDamage;
    private int maxDamage;
    private final float attackSpeed;

    private final long startTime = System.currentTimeMillis();

    private long currentDPS;
    private final NavigableMap<Long, Long> prevHP = new TreeMap<>(Collections.reverseOrder());

    public Tower(String name, String ownerTag, long hp, double defPercent, int minDamage, int maxDamage, float attackSpeed) {
        this.name = name;
        this.ownerTag = ownerTag;
        this.currentHP = hp;
        this.maxHP = hp;
        this.defPercent = defPercent;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.attackSpeed = attackSpeed;

        prevHP.put(System.currentTimeMillis(), hp);
    }

    public void updateDamage(int minDamage, int maxDamage) {
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public void updateHealth(long curHP) {
        this.currentHP = curHP;
        long time = System.currentTimeMillis();

        this.currentDPS = Optional.ofNullable(prevHP.lastEntry())
                .map(e -> (e.getValue() - curHP) * 1000 / (time - e.getKey()))
                .orElse(0L);

        prevHP.entrySet().removeIf(e -> time - e.getKey() >= 30 * 1000);
        prevHP.put(time, curHP);
    }

    public String toTitleString() {
        long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;

        String remTimeStr;
        if (currentDPS == 0) {
            remTimeStr = "inf";
        } else {
            long estTimeRemaining = currentHP / currentDPS;
            if (estTimeRemaining >= 60 * 60) {
                remTimeStr = "inf";
            } else {
                remTimeStr = String.format("%02d:%02d", estTimeRemaining / 60, estTimeRemaining % 60);
            }
        }

        long currentEHP = Math.round(currentHP / (1 - defPercent / 100));
        long towerDPS = Math.round((double) (minDamage + maxDamage) / 2 * attackSpeed);

        return String.format("§4❤ %s(%s)/%s §3-%.1f%% §7| §6☠ %s x%.1f = %s §7| §c⚔ %s ≈ %s §7| §d⌛ %02d:%02d",
                NumberFormatter.format(currentHP), NumberFormatter.format(currentEHP), NumberFormatter.format(maxHP), defPercent,
                NumberFormatter.format((minDamage + maxDamage) / 2), attackSpeed, NumberFormatter.format(towerDPS),
                NumberFormatter.format(currentDPS), remTimeStr,
                timeElapsed / 60, timeElapsed % 60);
    }

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }

    public double getHPPercent() {
        return 100d * prevHP.firstEntry().getValue() / maxHP;
    }

    public double getTotalTimeElapsed() {
        long timeElapsed = prevHP.firstKey() - startTime;
        return (double) timeElapsed / 1000;
    }

    public Text toText() {
        double timeElapsed = getTotalTimeElapsed();
        long damageDealt = maxHP - prevHP.firstEntry().getValue();
        long averageDPS = timeElapsed > 0 ? Math.round(damageDealt / timeElapsed) : 0;
        long averageEDPS = Math.round(averageDPS / (1 - defPercent / 100));

        MutableText name = Text.literal(this.name + ": ");
        MutableText hp = Text.literal(String.format("%d ", this.maxHP))
                .setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
        MutableText def = Text.literal(String.format("-%.1f%% ", this.defPercent))
                .setStyle(Style.EMPTY.withColor(Formatting.AQUA));
        MutableText dmg = Text.literal(String.format("%.1fx %d-%d\n", this.attackSpeed, this.minDamage, this.maxDamage))
                .setStyle(Style.EMPTY.withColor(Formatting.GOLD));
        MutableText dps = Text.literal(String.format("Average DPS: %d (%d)\n", averageDPS, averageEDPS));
        MutableText time = Text.literal(String.format("Time Elapsed: %02d:%04.1f", (int) Math.round(timeElapsed / 60), timeElapsed % 60));

        return name.append(hp).append(def).append(dmg).append(dps).append(time);
    }
}
