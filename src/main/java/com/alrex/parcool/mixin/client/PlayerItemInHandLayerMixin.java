package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.alrex.parcool.common.item.misc.GrapplingHookItem;
import com.alrex.parcool.fabric.compat.IAvatarRenderStateEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Third person: hides the held item when it is drawn as equipment or is a deployed grappling hook. */
@Mixin(PlayerItemInHandLayer.class)
public abstract class PlayerItemInHandLayerMixin {
    @Inject(method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("HEAD"), cancellable = true)
    private void onSubmitArmWithItem(AvatarRenderState state, ItemStackRenderState itemState, ItemStack stack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        var player = ((IAvatarRenderStateEntity) state).parcool$getPlayer();
        if (player == null) return;
        if (stack.getItem() instanceof EquipAble equipAble) {
            if (equipAble.renderWhenIn(player, arm)) ci.cancel();
        } else if (stack.getItem() instanceof GrapplingHookItem && GrapplingHookItem.isDeployed(player)) {
            ci.cancel();
        }
    }
}
