package io.unrealintegers.wynnutilitymod.modules.eco;

import com.mojang.blaze3d.systems.RenderSystem;
import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.keyinding.KeybindManager;
import io.unrealintegers.wynnutilitymod.models.*;
import io.unrealintegers.wynnutilitymod.util.NumberFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EcoOverlayScreen
        extends HandledScreen<EcoOverlayScreenHandler>
        implements ScreenHandlerProvider<EcoOverlayScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private static final float HSCALE = 4 / 3f;
    private static final float VSCALE = 2.4f;

    private static final Map<EcoOverlayMode, String> TITLES = Map.of(
            EcoOverlayMode.PRODUCTIONS, "Territory Productions",
            EcoOverlayMode.TOWER_LEVELS, "Tower Stats/Bonuses");
    private static final String[] LEGEND_LINES = {
            "§rName:",
            "  Color indicates defence,",
            "  Bold indicates multi-attacks,",
            "  Star indicates HQ conn,",
            "  Crossout indicates no route.",
            "",
            "Press <Cycle Display Mode> to change pages,",
            "  1. Productions",
            "  2. Tower Stats/Bonuses",
    };

    private static final List<Item> territoryItems = Arrays.asList(Items.GOLDEN_SHOVEL, Items.BARRIER,
            Items.DIAMOND, Items.PAPER, Items.MAP, Items.DIAMOND_AXE, Items.OAK_BUTTON, Items.TORCH);


    public static boolean guiCheck(Screen gui) {
        if (!(gui instanceof GenericContainerScreen)) {
            return false;
        }

        return gui.getTitle() != null && (gui.getTitle().getString().contains("Territories") || TITLES.containsValue(gui.getTitle().getString()));
    }

    private EcoOverlayMode currentMode;
    private LocalDateTime lastRender = LocalDateTime.now();

    public EcoOverlayScreen(EcoOverlayScreenHandler handler, PlayerInventory inventory) {
        super(handler, inventory, Text.literal(TITLES.get(EcoOverlayMode.PRODUCTIONS)));

        currentMode = EcoOverlayMode.PRODUCTIONS;
        this.backgroundWidth = Math.round(176 * HSCALE);
        this.backgroundHeight = Math.round(104 * VSCALE);
    }

    @Override
    public void init() {
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        KeybindManager.processGuiKeys(keyCode, scanCode, modifiers, this);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void cycle() {
        currentMode = currentMode.next();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        if (lastRender.plusSeconds(1).isBefore(LocalDateTime.now())) {
            WynnUtilityMod.LOGGER.info("Opening territory GUI.");
            currentMode = EcoOverlayMode.PRODUCTIONS;
        }
        lastRender = LocalDateTime.now();

        Inventory inv = this.handler.getInventory();

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        renderLegend(ctx, tr);

        for (int i = 0; i < inv.size(); ++i) {
            Slot slot = this.handler.getSlot(i);
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

                renderName(ctx, tr, slot, terr);

                switch (currentMode) {
                    case PRODUCTIONS -> renderProductions(ctx, tr, slot, terr);
                    case TOWER_LEVELS -> renderTowerLevels(ctx, tr, slot, terr);
                }
            } else {
                WynnUtilityMod.LOGGER.info(stripped);
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        this.drawMouseoverTooltip(ctx, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        int i = Math.round(this.x / HSCALE);
        int j = Math.round(this.y / VSCALE);

        ctx.getMatrices().push();
        ctx.getMatrices().scale(HSCALE, VSCALE, 1);

        ctx.drawTexture(TEXTURE, i, j, 0, 1, 176, 2);
        ctx.drawTexture(TEXTURE, i, j + 2, 0, 9, 176, 5 * 18 + 8);
        ctx.drawTexture(TEXTURE, i, j + 5 * 18 + 10, 0, 126, 176, 5);

        ctx.getMatrices().pop();
    }

    private void renderLegend(DrawContext ctx, TextRenderer tr) {
        int width = Arrays.stream(LEGEND_LINES).map(tr::getWidth).max(Integer::compareTo).orElse(0) + 10;
        int height = (LEGEND_LINES.length * tr.fontHeight) + 10;

        int x1 = this.x - 10 - width;
        int y1 = this.y + 94 - height / 2;
        int x2 = x1 + width;
        int y2 = y1 + height;

        ctx.fill(x1, y1, x2, y2, new Color(199, 199, 199, 91).getRGB());

        int tx = x1 + 5;
        int ty = y1 + 5;

        for (String line : LEGEND_LINES) {
            drawAt(ctx, tr, tx, ty, line);
            ty += tr.fontHeight;
        }
    }

    private void renderName(DrawContext ctx, TextRenderer tr, Slot slot, @NotNull Territory terr) {
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

        String upper = defenceLevel.getFormat() + (terr.distanceToHQ < 0 ? Formatting.STRIKETHROUGH : "") +
                (hasMulti ? Formatting.BOLD : "") + abbrev + (terr.distanceToHQ == 1 ? "*" : "");
        drawAbove(ctx, tr, slot, upper);
    }

    private void renderProductions(DrawContext ctx, TextRenderer tr, Slot slot, @NotNull Territory terr) {
        boolean isCity = terr.isCity();
        boolean isDouble = terr.isDouble();

        TerritoryProductionType primaryProduction = terr.getPrimaryProduction();
        float resourceEfficiency = 1 + 0.5f * terr.upgrades.get(TerritoryUpgradeType.EFFICIENT_RESOURCES, 0);
        float resourceSpeed = 4 - terr.upgrades.get(TerritoryUpgradeType.RESOURCE_RATE, 0);
        int resourceProduction = Math.round(3600 * resourceEfficiency * 4 / resourceSpeed * (isDouble ? 2 : 1)
                * (1 + terr.getTreasuryPct() / 100) / (primaryProduction == TerritoryProductionType.OASIS ? 4 : 1));

        float emeraldEfficiency = 1 + 0.5f * terr.upgrades.get(TerritoryUpgradeType.EFFICIENT_EMERALDS, 0);
        float emeraldSpeed = 4 - terr.upgrades.get(TerritoryUpgradeType.EMERALD_RATE, 0);
        int emeraldProduction = Math.round(9000 * emeraldEfficiency * 4 / emeraldSpeed * (isCity ? 2 : 1)
                * (1 + terr.getTreasuryPct() / 100));

        String upper = primaryProduction.getFormat() + (isDouble ? Formatting.BOLD : "") +
                NumberFormatter.format(resourceProduction);
        String lower = ResourceType.EMERALDS.getFormat() + (isCity ? Formatting.BOLD : "") +
                NumberFormatter.format(emeraldProduction);

        drawBelow(ctx, tr, slot, upper, 1);
        drawBelow(ctx, tr, slot, lower, 2);
    }

    private void renderTowerLevels(DrawContext ctx, TextRenderer tr, Slot slot, @NotNull Territory terr) {
        String damage = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_DAMAGE, 0));
        String attack = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_ATTACK, 0));
        String health = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_HEALTH, 0));
        String defence = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_DEFENCE, 0));

        String minion = convertInt(terr.upgrades.get(TerritoryUpgradeType.STRONGER_MINIONS, 0));
        String multi = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_MULTI_ATTACKS, 0));
        String aura = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_AURA, 0));
        String volley = convertInt(terr.upgrades.get(TerritoryUpgradeType.TOWER_VOLLEY, 0));

        String upper = "" + Formatting.RED + damage + attack + health + defence;
        String lower = "" + Formatting.RED + minion + multi + aura + volley;

        drawBelow(ctx, tr, slot, upper, 1);
        drawBelow(ctx, tr, slot, lower, 2);
    }

    private void drawAbove(DrawContext ctx, TextRenderer tr, Slot slot, String text) {
        float i = this.x + slot.x + 8 - tr.getWidth(text) / 2f;
        float j = this.y + slot.y + 1 - tr.fontHeight;

        drawAt(ctx, tr, i, j, text);
    }

    private void drawBelow(DrawContext ctx, TextRenderer tr, Slot slot, String text, int row) {
        float i = this.x + slot.x + 8 - tr.getWidth(text) / 2f;
        float j = this.y + slot.y + 15 + (row - 1) * tr.fontHeight;

        drawAt(ctx, tr, i, j, text);
    }

    private void drawAt(DrawContext ctx, TextRenderer tr, float x, float y, String text) {
        tr.draw(text, x, y, Colors.WHITE, true, ctx.getMatrices().peek().getPositionMatrix(),
                ctx.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
    }

    private String convertInt(Integer n) {
        assert n >= 0;
        assert n <= 11;
        return n == 11 ? "Z" : x == 10 ? "X" : n.toString();
    }
}
