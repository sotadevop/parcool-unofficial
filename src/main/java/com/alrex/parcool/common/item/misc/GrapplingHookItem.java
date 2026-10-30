package com.alrex.parcool.common.item.misc;

import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class GrapplingHookItem extends Item {
    public GrapplingHookItem(Properties properties) {
        super(properties);
    }

    public static boolean isHeld(Player player) {
        return player.getMainHandItem().getItem() instanceof GrapplingHookItem;
    }

    public static boolean isDeployed(net.minecraft.world.entity.LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        var parkourability = com.alrex.parcool.common.Parkourability.get(player);
        return parkourability != null
                && parkourability.get(com.alrex.parcool.common.action.ParCoolActions.GRAPPLE).isDoing();
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull Level level, Player player, @Nonnull InteractionHand hand) {
        return InteractionResult.PASS;
    }

    // Fabric port: the custom item renderer (flat icon in GUIs, 3D model in hand) is a
    // "minecraft:select" on the display context in assets/parcool/items/grappling_hook.json.


    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.throw").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.reel").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.steer").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.release").withStyle(ChatFormatting.GRAY));
    }
}
