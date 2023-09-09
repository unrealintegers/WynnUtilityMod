package io.unrealintegers.wynnutilitymod.modules.war;


import io.unrealintegers.wynnutilitymod.models.Tower;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class TowerManager {
    private Tower currentTower;
    private final BossBarListener bossBarListener;

    public TowerManager() {
        bossBarListener = new BossBarListener();

        ClientTickEvents.START_CLIENT_TICK.register(this::tick);
    }

    public void tick(MinecraftClient client) {
        if (currentTower == null) return;
        if (MinecraftClient.getInstance().player == null) return;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        int posX = player.getBlockX();
        int posZ = player.getBlockZ();
        if (posX < -66000 || posX > -65000 || posZ > -56000 || posZ < -74000) {
            player.sendMessage(currentTower.toText());
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
