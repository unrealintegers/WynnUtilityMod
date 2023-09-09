package io.unrealintegers.wynnutilitymod.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUtils {
    // TODO: Catalogue Wynncraft Item Textures

    public static void replaceItemInInventory(Inventory inventory, int slot, Item newType) {
        replaceItemInInventory(inventory, slot, newType, 0);
    }

    public static void replaceItemInInventory(Inventory inventory, int slot, Item newType, int damage) {
        ItemStack oldIStack = inventory.getStack(slot);
        ItemStack newIStack = new ItemStack(newType, oldIStack.getCount());
        newIStack.setNbt(oldIStack.getNbt());
        newIStack.setDamage(damage);
        inventory.setStack(slot, newIStack);
    }
}
