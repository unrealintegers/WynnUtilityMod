package io.unrealintegers.wynnutilitymod.mixin;

import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.models.Territory;
import io.unrealintegers.wynnutilitymod.models.Wynncraft;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ClientAdvancementManager.class)
public abstract class AdvancementMixin {
    @Inject(method = "onAdvancements", at = @At("HEAD"))
    private void onAdvancements(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
        WynnUtilityMod.LOGGER.info("Received " + packet.getAdvancementsToEarn().size() + " advancements.");

        packet.getAdvancementsToEarn().forEach(
                (entry) -> {
                    Advancement advancement = entry.value();
                    AdvancementDisplay display = advancement.display().orElse(null);

                    if (display == null) {
                        WynnUtilityMod.LOGGER.info("Advancement " + entry.id() + " has no display.");
                        return;
                    }

                    String territoryName = display.getTitle().getString().trim();
                    Territory territory = Wynncraft.getTerritory(territoryName);

                    if (territory == null) {
                        WynnUtilityMod.LOGGER.info("Advancement " + territoryName + " has no territory.");
                        return;
                    }

                    territory.parseAdvancementDisplay(display);
                }
        );

        WynnUtilityMod.LOGGER.info("HQs: " + Wynncraft.getTerritories().values().stream().filter(t -> t.isHQ).count());

        Map<String, Integer> connectedTerritories = Wynncraft.getTerritories()
                .values()
                .stream()
                .filter(t -> t.isHQ)
                .map(t -> t.bfs((t1, d) -> t.getOwner().equals(t1.getOwner())))
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        WynnUtilityMod.LOGGER.info("Connected: " + connectedTerritories.size());

        Wynncraft.getTerritories()
                .values()
                .forEach(t -> t.distanceToHQ = connectedTerritories.getOrDefault(t.getName(), -1));
    }
}
