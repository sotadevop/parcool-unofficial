package com.alrex.parcool.common.item.recipe.special;

import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class ZiplineRopeDyeRecipe extends CustomRecipe {

    /** 26.3: custom recipes no longer carry a crafting book category of their own. */
    public ZiplineRopeDyeRecipe() {
        super();
    }

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        boolean ziplineRopeFound = false;
        boolean dyeItemFound = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem().equals(Items.ZIPLINE_ROPE.get())) {
                if (ziplineRopeFound) return false;
                else ziplineRopeFound = true;
            } else if (stack.has(DataComponents.DYE)) {
                dyeItemFound = true;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }
        return ziplineRopeFound && dyeItemFound;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingInput input) {
        ItemStack ziplineRope = null;
        LinkedList<DyeColor> dyeItems = new LinkedList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            Item item = stack.getItem();
            if (item instanceof ZiplineRopeItem) {
                ziplineRope = stack;
            } else if (stack.has(DataComponents.DYE)) {
                // 26.3 stores a dye's colour as a data component instead of on DyeItem.
                dyeItems.add(stack.get(DataComponents.DYE));
            } else if (!stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        if (ziplineRope == null || dyeItems.isEmpty()) return ItemStack.EMPTY;
        ItemStack resultZiplineRope = new ItemStack(Items.ZIPLINE_ROPE::get);
        resultZiplineRope.applyComponents(resultZiplineRope.getComponents());
        int r = 0, g = 0, b = 0;
        int dyeSize = dyeItems.size();
        for (DyeColor dyeColor : dyeItems) {
            int color = dyeColor.getTextureDiffuseColor();
            r += ARGB.red(color);
            g += ARGB.green(color);
            b += ARGB.blue(color);
        }
        if (ZiplineRopeItem.hasCustomColor(resultZiplineRope)) {
            dyeSize++;
            int color = ZiplineRopeItem.getColor(resultZiplineRope);
            r += ARGB.red(color);
            g += ARGB.green(color);
            b += ARGB.blue(color);
        }
        r = Mth.clamp(r / dyeSize, 0, 0xFF);
        g = Mth.clamp(g / dyeSize, 0, 0xFF);
        b = Mth.clamp(b / dyeSize, 0, 0xFF);
        ZiplineRopeItem.setColor(resultZiplineRope, ARGB.color(r, g, b));
        return resultZiplineRope;
    }

    // 1.21.11 dropped CustomRecipe#canCraftInDimensions; the recipe no longer declares a grid size
    // limit. `matches` already requires exactly one rope and at least one dye, which is what the old
    // "width * height >= 2" check was standing in for.

    @Nonnull
    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return Recipes.ZIPLINE_ROPE_DYE.get();
    }
}
