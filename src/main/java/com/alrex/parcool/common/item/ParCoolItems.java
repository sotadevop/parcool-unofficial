package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.item.armor.TraceurBootsItem;
import com.alrex.parcool.common.item.armor.TraceurGlovesItem;
import com.alrex.parcool.common.item.misc.GrapplingHookItem;
import com.alrex.parcool.common.item.misc.ParCoolGuideItem;
import com.alrex.parcool.common.item.misc.ZiplineRopeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.IEventBus;
import com.alrex.parcool.fabric.compat.registry.DeferredHolder;
import com.alrex.parcool.fabric.compat.registry.DeferredRegister;

public class ParCoolItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ParCool.MOD_ID);
    public static final DeferredHolder<Item, BlockItem> WOODEN_ZIPLINE_HOOK = ITEMS.register("wooden_zipline_hook", () -> new BlockItem(Blocks.WOODEN_ZIPLINE_HOOK.get(), new Item.Properties().setId(ITEMS.key("wooden_zipline_hook")).useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> IRON_ZIPLINE_HOOK = ITEMS.register("iron_zipline_hook", () -> new BlockItem(Blocks.IRON_ZIPLINE_HOOK.get(), new Item.Properties().setId(ITEMS.key("iron_zipline_hook")).useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, ZiplineRopeItem> ZIPLINE_ROPE = ITEMS.register("zipline_rope", () -> new ZiplineRopeItem(new Item.Properties().setId(ITEMS.key("zipline_rope"))));
    public static final DeferredHolder<Item, Item> HOOK = ITEMS.register("hook", () -> new Item(new Item.Properties().setId(ITEMS.key("hook"))));
    public static final DeferredHolder<Item, GrapplingHookItem> GRAPPLING_HOOK = ITEMS.register("grappling_hook", () -> new GrapplingHookItem(new Item.Properties().setId(ITEMS.key("grappling_hook")).stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item, TraceurGlovesItem> TRACEUR_GLOVES = ITEMS.register("traceur_gloves", () -> new TraceurGlovesItem(new Item.Properties().setId(ITEMS.key("traceur_gloves")).stacksTo(1)));
    public static final DeferredHolder<Item, TraceurBootsItem> TRACEUR_BOOTS = ITEMS.register("traceur_boots", () -> new TraceurBootsItem(new Item.Properties().setId(ITEMS.key("traceur_boots")).stacksTo(1)));
    public static final DeferredHolder<Item, ParCoolGuideItem> PARCOOL_GUIDE = ITEMS.register("parcool_guide", () -> new ParCoolGuideItem(new Item.Properties().setId(ITEMS.key("parcool_guide")).stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    // Fabric port: item tints are data in 26.x (assets/parcool/items/*.json, "minecraft:dye").
    public static void registerColors() {
    }
}