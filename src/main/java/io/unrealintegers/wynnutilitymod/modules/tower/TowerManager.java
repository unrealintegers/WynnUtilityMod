package io.unrealintegers.wynnutilitymod.modules.tower;

import io.unrealintegers.wynnutilitymod.model.Tower;
import net.minecraft.client.Minecraft;

public class TowerManager {
    private Tower currentTower;

    public void tick() {
        if (currentTower == null) return;
        if (Minecraft.getMinecraft().player == null) return;

        int posX = Minecraft.getMinecraft().player.getPosition().getX();
        int posZ = Minecraft.getMinecraft().player.getPosition().getZ();
        if (posX < -68000 || posX > -63000 || posZ < -68000 || posZ > -63000) {
            Minecraft.getMinecraft().player.sendMessage(currentTower.toChatComponent());
            reset();
        }
    }

    void reset() {
        currentTower = null;
    }

    String update(String tag, String territoryName, long currentHP, double defPercent, int minDamage, int maxDamage, float attackSpeed) {
        if (currentTower == null) {
            currentTower = new Tower(territoryName, tag, currentHP, defPercent, minDamage, maxDamage, attackSpeed);
        } else {
            currentTower.updateDamage(minDamage, maxDamage);
            currentTower.updateHealth(currentHP);
        }

        return currentTower.toTitleString();
    }
}
