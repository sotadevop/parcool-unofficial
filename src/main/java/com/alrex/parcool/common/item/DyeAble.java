package com.alrex.parcool.common.item;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface DyeAble {

    int getDefaultColor();

    default void setColor(ItemStack stack, int color) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
    }

    default int getColor(ItemStack stack) {
        var colorComp = stack.get(DataComponents.DYED_COLOR);
        return (colorComp == null ? getDefaultColor() : colorComp.rgb()) | 0xFF000000;
    }

    default boolean hasCustomColor(ItemStack stack) {
        return stack.has(DataComponents.DYED_COLOR);
    }

    static void appendHoverText(DyeAble dyeAble, @Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull Consumer<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        var colorComp = stack.get(DataComponents.DYED_COLOR);
        if (colorComp != null) {
            colorComp.addToTooltip(context, lines, tooltipFlag, stack);
        } else {
            lines.accept(Component.translatable("parcool.gui.text.dyeable.dyeable").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
