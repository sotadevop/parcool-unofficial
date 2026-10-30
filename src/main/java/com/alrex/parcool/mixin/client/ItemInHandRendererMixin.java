package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.alrex.parcool.common.item.misc.GrapplingHookItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * First person: also draws the bare arm when it holds equipment that is shown on the arm (traceur
 * gloves) or a deployed grappling hook. Upstream popped the item transform and drew the arm right
 * before the item; at the head of submitArmWithItem the pose is that same base pose. 26.x renamed
 * ItemInHandRenderer to FirstPersonHandsAndItemsRenderer and passes render states; first person is
 * always the local player.
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, SubmitNodeCollector collector, int light, float equipProgress, float attackProgress, HumanoidArm arm, PlayerRenderState playerState);

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void onSubmitArmWithItem(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState handsState, float frameInterp, float xRot, InteractionHand hand, float attackProgress, ItemStack stack, float equipProgress, PoseStack poseStack, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player == null || stack.isEmpty()) return;
        var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean showBareArm = stack.getItem() instanceof EquipAble equipAble
                ? equipAble.renderWhenIn(player, arm)
                : stack.getItem() instanceof GrapplingHookItem && GrapplingHookItem.isDeployed(player);
        if (showBareArm) {
            poseStack.pushPose();
            this.renderPlayerArm(poseStack, collector, light, equipProgress, attackProgress, arm, playerState);
            poseStack.popPose();
        }
    }
}
