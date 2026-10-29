package com.alrex.parcool.common.item.recipe;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.recipe.special.ParCoolDyeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import com.alrex.parcool.fabric.compat.event.IEventBus;
import com.alrex.parcool.fabric.compat.registry.DeferredRegister;
import com.alrex.parcool.fabric.compat.registry.DeferredHolder;

public class Recipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ParCool.MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ParCoolDyeRecipe>> DYE_ITEM = RECIPES.register("dye_item", () -> new SimpleCraftingRecipeSerializer<>(ParCoolDyeRecipe::new));

    public static void register(IEventBus bus) {
        RECIPES.register(bus);
    }
}
