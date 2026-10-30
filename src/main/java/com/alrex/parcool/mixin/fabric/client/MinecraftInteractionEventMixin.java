package com.alrex.parcool.mixin.fabric.client;

import com.alrex.parcool.fabric.FabricClientEventBridge;
import com.alrex.parcool.fabric.compat.event.InputEvent.InteractionKeyMappingTriggered;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge's InputEvent.InteractionKeyMappingTriggered for attack and use-item. ParCool cancels it
 * while an action needs the mouse buttons (hanging, sliding, ...); a canceled interaction only
 * swings the hand when the handler asks for it, as on NeoForge.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftInteractionEventMixin {
    @Shadow
    public LocalPlayer player;

    @Inject(method = "startAttack()Z", at = @At("HEAD"), cancellable = true)
    private void parcool$attackEvent(CallbackInfoReturnable<Boolean> cir) {
        if (parcool$fire(InteractionKeyMappingTriggered.Kind.ATTACK)) cir.setReturnValue(false);
    }

    @Inject(method = "startUseItem()V", at = @At("HEAD"), cancellable = true)
    private void parcool$useItemEvent(CallbackInfo ci) {
        if (parcool$fire(InteractionKeyMappingTriggered.Kind.USE_ITEM)) ci.cancel();
    }

    private boolean parcool$fire(InteractionKeyMappingTriggered.Kind kind) {
        if (player == null) return false;
        var event = FabricClientEventBridge.fireInteraction(kind, InteractionHand.MAIN_HAND);
        if (!event.isCanceled()) return false;
        if (event.shouldSwingHand()) {
            // 26.x swings with the held item's animation, as vanilla's startAttack/startUseItem do.
            var stack = player.getItemInHand(event.getHand());
            var animation = kind == InteractionKeyMappingTriggered.Kind.ATTACK ? stack.getAttackAnimation() : stack.getInteractAnimation();
            player.swing(event.getHand(), animation, false);
        }
        return true;
    }
}
