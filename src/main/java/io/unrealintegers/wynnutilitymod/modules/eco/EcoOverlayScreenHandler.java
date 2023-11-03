package io.unrealintegers.wynnutilitymod.modules.eco;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class EcoOverlayScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final int NUM_ROWS = 5;

    public EcoOverlayScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(type, syncId);
        int r, c;
        GenericContainerScreenHandler.checkSize(inventory, NUM_ROWS * 9);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int x, y;
        for (r = 0; r < NUM_ROWS; ++r) {
            for (c = 0; c < 9; ++c) {
                x = c * 28 - 24;
                y = r * 44 + 32;

                if (c == 0) {
                    x = 11;
                    if (r == 4) {
                        y = 225;
                    }
                } else if (c == 1) {
                    x = -20;
                }

                this.addSlot(new Slot(inventory, r * 9 + c, x, y));
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < NUM_ROWS * 9 ? !this.insertItem(itemStack2, NUM_ROWS * 9, this.slots.size(), true) : !this.insertItem(itemStack2, 0, NUM_ROWS * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
