package com.alrex.parcool.common.item.recipe.special;

import net.minecraft.core.component.DataComponents;

import com.alrex.parcool.common.item.DyeAble;
import com.alrex.parcool.common.item.recipe.Recipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class ParCoolDyeRecipe extends CustomRecipe {
    public ParCoolDyeRecipe() {
        super();
    }

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        boolean dyeAbleItemFound = false;
        boolean dyeItemFound = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof DyeAble) {
                if (dyeAbleItemFound) return false;
                else dyeAbleItemFound = true;
            } else if (stack.has(DataComponents.DYE)) {
                dyeItemFound = true;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }
        return dyeAbleItemFound && dyeItemFound;
    }

    @Override
    public ItemStack assemble(@Nonnull CraftingInput input) {
        ItemStack dyeAbleStack = null;
        DyeAble dyeAbleItem = null;
        LinkedList<DyeColor> dyeItems = new LinkedList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            Item item = stack.getItem();
            if (item instanceof DyeAble dyeAble) {
                dyeAbleStack = stack;
                dyeAbleItem = dyeAble;
            } else if (stack.has(DataComponents.DYE)) {
                // 26.x stores a dye's colour as a data component instead of on DyeItem.
                dyeItems.add(stack.get(DataComponents.DYE));
            } else if (!stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        if (dyeAbleStack == null || dyeItems.isEmpty()) return ItemStack.EMPTY;
        ItemStack resultItem = new ItemStack(dyeAbleStack.getItem());
        resultItem.applyComponents(dyeAbleStack.getComponents());

        int r = 0, g = 0, b = 0;
        int dyeSize = dyeItems.size();
        for (DyeColor color : dyeItems) {
            r += (color.getTextureDiffuseColor() >> 16) & 0xFF;
            g += (color.getTextureDiffuseColor() >> 8) & 0xFF;
            b += color.getTextureDiffuseColor() & 0xFF;
        }
        if (dyeAbleItem.hasCustomColor(resultItem)) {
            dyeSize++;
            int color = dyeAbleItem.getColor(resultItem);
            r += (color & 0xFF0000) >> 16;
            g += (color & 0x00FF00) >> 8;
            b += (color & 0x0000FF);
        }
        r = Mth.clamp(r / dyeSize, 0, 0xFF);
        g = Mth.clamp(g / dyeSize, 0, 0xFF);
        b = Mth.clamp(b / dyeSize, 0, 0xFF);
        dyeAbleItem.setColor(resultItem, (r << 16) + (g << 8) + b);
        return resultItem;
    }


    @Nonnull
    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return Recipes.DYE_ITEM.get();
    }
}
