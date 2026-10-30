package com.alrex.parcool.common.item.misc;

import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

import net.minecraft.world.InteractionResult;
import com.alrex.parcool.client.gui.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

public class ParCoolGuideItem extends Item {
    public ParCoolGuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        lines.accept(Component.translatable("parcool.gui.text.guide.tooltip.guide").withStyle(ChatFormatting.GRAY));
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        var itemInHand = player.getItemInHand(hand);
        if (level.isClientSide()) {
            GuiHelper.openGuideGui();
        }
        return InteractionResult.SUCCESS;
    }
}
