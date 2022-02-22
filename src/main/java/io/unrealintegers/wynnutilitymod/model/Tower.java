package io.unrealintegers.wynnutilitymod.model;

import io.unrealintegers.wynnutilitymod.utils.NumberFormatter;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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

    public ITextComponent toChatComponent() {
        long timeElapsed = prevHP.firstKey() - startTime;
        long damageDealt = maxHP - prevHP.firstEntry().getValue();
        long averageDPS = timeElapsed > 0 ? damageDealt * 1000 / timeElapsed : 0;
        long averageEDPS = Math.round(averageDPS / (1 - defPercent / 100));
        double timeElapsedD = (double) Math.round((double) timeElapsed / 100) / 10;  // ms -> s

        ITextComponent name = new TextComponentString(this.name + ": ");
        ITextComponent hp = new TextComponentString(String.format("%d ", this.maxHP)).setStyle(new Style().setColor(TextFormatting.DARK_RED));
        ITextComponent def = new TextComponentString(String.format("-%.1f%% ", this.defPercent)).setStyle(new Style().setColor(TextFormatting.AQUA));
        ITextComponent dmg = new TextComponentString(String.format("%.1fx %d-%d\n", this.attackSpeed, this.minDamage, this.maxDamage)).setStyle(new Style().setColor(TextFormatting.GOLD));
        ITextComponent dps = new TextComponentString(String.format("Average DPS: %d (%d)\n", averageDPS, averageEDPS));
        ITextComponent time = new TextComponentString(String.format("Time Elapsed: %02d:%04.1f", (int) timeElapsedD / 60, timeElapsedD % 60));

        return name.appendSibling(hp).appendSibling(def).appendSibling(dmg).appendSibling(dps).appendSibling(time);
    }
}
