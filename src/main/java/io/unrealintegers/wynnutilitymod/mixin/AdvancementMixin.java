package io.unrealintegers.wynnutilitymod.mixin;

import io.unrealintegers.wynnutilitymod.models.Territory;
import io.unrealintegers.wynnutilitymod.models.Wynncraft;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
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
        packet.getAdvancementsToEarn().forEach(
                (id, builder) -> {
                    Advancement advancement = builder.build(id);
                    AdvancementDisplay display = advancement.getDisplay();

                    if (display == null) return;

                    String territoryName = display.getTitle().getString();
                    Territory territory = Wynncraft.getTerritory(territoryName);

                    if (territory == null) return;

                    territory.parseAdvancementDisplay(display);
                }
        );

        Set<String> connectedTerritories = Wynncraft.getTerritories()
                .values()
                .stream()
                .filter(t -> t.isHQ)
                .map(t -> t.bfs((t1, d) -> t.getOwner().equals(t1.getOwner())))
                .map(Map::keySet)
                .flatMap(Set::stream)
                .collect(Collectors.toUnmodifiableSet());

        Wynncraft.getTerritories()
                .values()
                .forEach(t -> t.isConnectedToHQ = connectedTerritories.contains(t.getName()));
    }
}
