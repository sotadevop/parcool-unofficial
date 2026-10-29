package com.alrex.parcool.common.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, Player player, @Nonnull InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    // Fabric port: NeoForge's IClientItemExtensions hook is ported with the 26.3 rendering code
    // (see porting/NOTES.md, "Open").

    @Environment(EnvType.CLIENT)
    private static final class ClientRendererHolder {
        private static com.alrex.parcool.client.renderer.GrapplingHookItemRenderer renderer;

        private static com.alrex.parcool.client.renderer.GrapplingHookItemRenderer get() {
            if (renderer == null) renderer = new com.alrex.parcool.client.renderer.GrapplingHookItemRenderer();
            return renderer;
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.throw").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.reel").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.steer").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.release").withStyle(ChatFormatting.GRAY));
    }
}
