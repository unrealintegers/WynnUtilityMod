package io.unrealintegers.wynnutilitymod.modules.eco;

import com.mojang.blaze3d.systems.RenderSystem;
import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.models.*;
import io.unrealintegers.wynnutilitymod.util.ItemUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EcoOverlayManager {
    private static final Map<EcoOverlayMode, String> TITLES = Map.of(EcoOverlayMode.NAME_DEF, "Name // Def (Extras)", EcoOverlayMode.PRODUCTIONS, "Em/Res Storage/Eff/Speed", EcoOverlayMode.TOWER_LEVELS, "Tower Stats/Bonuses");
    private static final String[] LEGEND_LINES = {
            "§rName (Top Left):",
            "  Color indicates treasury.",
            "§rEmeralds (Top Right):",
            "  §r§2$: Is City",
            "§rResources (Bottom Left):",
            "  §r§7\u24b7: Ore",
            "  §r§6\u24b8: Wood",
            "  §r§b\u24c0: Fish",
            "  §r§e\u24bf: Crops",
            "  §r§d\u2b1f: Oasis",
            "  Size indicates production.",
            "§rDefenses (Bottom Right):",
            "  §r§b-: Very Low",
            "  §r§aL: Low",
            "  §r§eM: Medium",
            "  §r§cH: High",
            "  §r§5V: Very High",
            "  §r+: Multi"
    };

    private static final Item GOLDEN_SHOVEL = Registries.ITEM.get(new Identifier("minecraft", "golden_shovel"));
    private static final Item BARRIER = Registries.ITEM.get(new Identifier("minecraft", "barrier"));
    private static final Item DIAMOND = Registries.ITEM.get(new Identifier("minecraft", "diamond"));
    private static final Item PAPER = Registries.ITEM.get(new Identifier("minecraft", "paper"));
    private static final Item MAP = Registries.ITEM.get(new Identifier("minecraft", "map"));
    private static final Item DIAMOND_AXE = Registries.ITEM.get(new Identifier("minecraft", "diamond_axe"));
    private static final List<Item> territoryItems = Arrays.asList(GOLDEN_SHOVEL, BARRIER, DIAMOND, PAPER, MAP, DIAMOND_AXE);


    public static boolean guiCheck(Screen gui) {
        if (!(gui instanceof GenericContainerScreen)) {
            return false;
        }

        return gui.getTitle() != null && (gui.getTitle().getString().contains("Territories") || TITLES.containsValue(gui.getTitle().getString()));
    }

    private EcoOverlayMode currentMode;
    private LocalDateTime lastRender = LocalDateTime.now();

    public EcoOverlayManager() {
        currentMode = EcoOverlayMode.NAME_DEF;
    }

    public void cycle() {
        currentMode = currentMode.next();
    }

    public void preRender(DrawContext ctx, Screen screen) {
        screen.title = Text.literal(TITLES.get(currentMode));
    }

    public void render(DrawContext ctx, GenericContainerScreen screen) {
        if (lastRender.plusSeconds(1).isBefore(LocalDateTime.now())) {
            WynnUtilityMod.LOGGER.info("Opening territory GUI.");
            currentMode = EcoOverlayMode.NAME_DEF;
        }
        lastRender = LocalDateTime.now();


        Inventory inv = screen.getScreenHandler().getInventory();
        int sx = screen.x;
        int sy = screen.y;

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        renderLegend(ctx, sx, sy, tr);

        for (int i = 0; i < inv.size(); ++i) {
            Slot slot = screen.getScreenHandler().getSlot(i);
            ItemStack istack = inv.getStack(i);
            Item item = istack.getItem();

            boolean isHQ = false;
            if (!territoryItems.contains(item)) {
                continue;
            }

            // Get rid of formatting codes, and get rid of HQ tags
            String name = istack.getName().getString();
            if (name.contains("HQ")) {
                isHQ = true;
            }

            String stripped = StringHelper.stripTextFormat(name).replaceAll(" *\\(HQ\\)", "").replaceAll("À", "").replaceAll("\\[!] ", "");
            if (stripped.equals("Territory Loadouts") || stripped.equals("Back") ||
                    stripped.equals("Previous Page") || stripped.equals("Next Page")) {
                continue;
            }

            Territory terr = Wynncraft.getTerritory(stripped);
            if (terr != null) {
                terr.isHQ = isHQ;
                terr.parseItemText(istack.getTooltip(MinecraftClient.getInstance().player, TooltipContext.BASIC));

                if (terr.isHQ) {
                    ItemUtils.replaceItemInInventory(inv, i, DIAMOND);
                } else if (terr.isConnectedToHQ) {
                    if (item == DIAMOND_AXE) {
                        ItemUtils.replaceItemInInventory(inv, i, GOLDEN_SHOVEL, 21);
                    } else {
                        ItemUtils.replaceItemInInventory(inv, i, GOLDEN_SHOVEL, 17);

                    }
                } else {
                    ItemUtils.replaceItemInInventory(inv, i, BARRIER);
                }

                switch (currentMode) {
                    case NAME_DEF -> renderNameDef(ctx, sx, sy, tr, slot, terr);
                    case PRODUCTIONS -> renderProductions(ctx, sx, sy, tr, slot, terr);
                    case TOWER_LEVELS -> renderTowerLevels(ctx, sx, sy, tr, slot, terr);
                }
            } else {
                WynnUtilityMod.LOGGER.info(stripped);
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
    }

    private void renderLegend(DrawContext ctx, int sx, int sy, TextRenderer tr) {
        float scale = 0.8f;

        int width = (int) (Arrays.stream(LEGEND_LINES).map(tr::getWidth).max(Integer::compareTo).orElse(0) * scale) + 10;
        int height = (int) (LEGEND_LINES.length * tr.fontHeight * scale) + 10;

        int x1 = sx - 10 - width;
        int y1 = sy + 94 - height / 2;
        int x2 = x1 + width;
        int y2 = y1 + height;

        ctx.fill(x1, y1, x2, y2, new Color(199, 199, 199, 91).getRGB());

        int tx = x1 + 5;
        int ty = y1 + 5;

        for (String line : LEGEND_LINES) {
            drawAt(ctx, tr, tx, ty, line, Color.white, scale);
            ty += tr.fontHeight * scale;
        }
    }

    private void renderNameDef(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, @NotNull Territory terr) {
        String abbrev;
        var parts = Arrays.stream(terr.getName().split(" ")).filter(s -> s.substring(0, 1).matches("[A-Z]")).toList();
        if (parts.size() == 1) {
            abbrev = parts.get(0).substring(0, 3);
        } else if (parts.size() == 2) {
            abbrev = parts.get(0).charAt(0) + parts.get(1).substring(0, 2);
        } else {
            abbrev = parts.stream().filter(s -> s.substring(0, 1).matches("[A-Z]")).map(s -> s.substring(0, 1)).collect(Collectors.joining());
            int endIndex = Math.min(abbrev.length(), 3);
            abbrev = abbrev.substring(0, endIndex);
        }

        DefenceLevel defenceLevel = terr.getDefenceLevel();
        boolean hasMulti = terr.upgrades.get(TerritoryUpgradeType.TOWER_MULTI_ATTACKS, 0) > 0;

        TerritoryProductionType primaryProduction = terr.getPrimaryProduction();
        int productionUpgradeMultiplier = terr.upgrades.get(TerritoryUpgradeType.EFFICIENT_RESOURCES, 0) +
                terr.upgrades.get(TerritoryUpgradeType.RESOURCE_RATE, 0);
        float textureScale = 0.65f + 0.075f * productionUpgradeMultiplier;
        boolean isCity = terr.isCity();
        boolean isDouble = terr.isDouble();

        String resourceSymbol = terr.getPrimaryProduction().Symbol.toString();

        String lr = defenceLevel.getDisplayText() + (hasMulti ? "+" : "");
        String ul = abbrev;

        if (isDouble) {
            drawLLCorner(ctx, sx, sy, tr, slot, " " + resourceSymbol, primaryProduction.Color, textureScale);
        } else if (isCity) {
            int emeraldUpgradeMultiplier = terr.upgrades.get(TerritoryUpgradeType.EFFICIENT_EMERALDS, 0) +
                    terr.upgrades.get(TerritoryUpgradeType.EMERALD_RATE, 0);
            float emeraldTextureScale = 0.65f + 0.075f * emeraldUpgradeMultiplier;
            ul = ul.substring(0, 2);
            drawURCorner(ctx, sx, sy, tr, slot, "$", ResourceType.EMERALDS.Color, emeraldTextureScale);
        }

        drawLLCorner(ctx, sx, sy, tr, slot, resourceSymbol, primaryProduction.Color, textureScale);
        drawLRCorner(ctx, sx, sy, tr, slot, lr, defenceLevel.getDisplayColor(), 0.8f);
        drawULCorner(ctx, sx, sy, tr, slot, ul, terr.getTreasuryColor(), 0.8f);
    }

    private void renderProductions(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, @NotNull Territory terr) {
        String emProd = "" + convertInt(terr.upgrades.get(TerritoryUpgradeType.LARGER_EMERALD_STORAGE, 0)) + convertInt(terr.upgrades.get(TerritoryUpgradeType.EFFICIENT_EMERALDS, 0)) + convertInt(terr.upgrades.get(TerritoryUpgradeType.EMERALD_RATE, 0));
        String resProd = "" + convertInt(terr.upgrades.get(TerritoryUpgradeType.LARGER_RESOURCE_STORAGE, 0)) + convertInt(terr.upgrades.get(TerritoryUpgradeType.EFFICIENT_RESOURCES, 0)) + convertInt(terr.upgrades.get(TerritoryUpgradeType.RESOURCE_RATE, 0));

        // Hide the strings if they are 000
        emProd = (emProd.equals("000")) ? "" : emProd;
        resProd = (resProd.equals("000")) ? "" : resProd;

        drawULCorner(ctx, sx, sy, tr, slot, emProd, ResourceType.EMERALDS.Color, 0.8f);
        drawLLCorner(ctx, sx, sy, tr, slot, resProd, ResourceType.EMERALDS.Color, 0.8f);
    }

    private void renderTowerLevels(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, @NotNull Territory terr) {
        String damage = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_DAMAGE, 0));
        String attack = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_ATTACK, 0));
        String health = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_HEALTH, 0));
        String defence = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_DEFENCE, 0));

        String minion = convertInt(terr.upgrades.get(TerritoryUpgradeType.STRONGER_MINIONS, 0));
        String multi = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_MULTI_ATTACKS, 0));
        String aura = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_AURA, 0));
        String volley = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_VOLLEY, 0));

        String upper = damage + attack + health + defence;
        String lower = minion + multi + aura + volley;

        drawULCorner(ctx, sx, sy, tr, slot, upper, new Color(0xaa0000), 0.6f);
        drawLLCorner(ctx, sx, sy, tr, slot, lower, new Color(0xaa0000), 0.6f);
    }

    private void drawULCorner(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, String text, Color color, float scale) {
        float x = sx + slot.x - 1;
        float y = sy + slot.y - 1;

        drawAt(ctx, tr, x, y, text, color, scale);
    }

    private void drawLLCorner(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, String text, Color color, float scale) {
        float x = sx + slot.x - 1;
        float y = sy + slot.y + 17 - tr.fontHeight * scale;

        drawAt(ctx, tr, x, y, text, color, scale);
    }

    private void drawURCorner(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, String text, Color color, float scale) {
        float x = sx + slot.x + 17 - tr.getWidth(text) * scale;
        float y = sy + slot.y - 1;

        drawAt(ctx, tr, x, y, text, color, scale);
    }

    private void drawLRCorner(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, String text, Color color, float scale) {
        float x = sx + slot.x + 17 - tr.getWidth(text) * scale;
        float y = sy + slot.y + 17 - tr.fontHeight * scale;

        drawAt(ctx, tr, x, y, text, color, scale);
    }

    private void drawMU(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, String text, Color color, float scale) {
        float x = sx + slot.x + 8 - tr.getWidth(text) * scale / 2;
        float y = sy + slot.y + 8 - tr.fontHeight * scale;

        drawAt(ctx, tr, x, y, text, color, scale);
    }

    private void drawML(DrawContext ctx, int sx, int sy, TextRenderer tr, Slot slot, String text, Color color, float scale) {
        float x = sx + slot.x + 8 - tr.getWidth(text) * scale / 2;
        float y = sy + slot.y + 8;

        drawAt(ctx, tr, x, y, text, color, scale);
    }

    private void drawAt(DrawContext ctx, TextRenderer tr, float x, float y, String text, Color color, float scale) {
        ctx.getMatrices().push();
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.getMatrices().translate(0, 0, 350);

        tr.draw(text, x / scale, y / scale, color.getRGB(), true, ctx.getMatrices().peek().getPositionMatrix(), ctx.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        ctx.getMatrices().pop();
    }

    private String convertInt(Integer x) {
        assert x >= 0;
        assert x <= 11;
        return x == 11 ? "Z" : x == 10 ? "X" : x.toString();
    }
}
