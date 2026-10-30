package com.alrex.parcool.mixin.client;

import com.alrex.parcool.fabric.compat.IAvatarRenderStateEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * extractRenderState is the last point where the renderer still holds the entity; it runs once per
 * frame before any submit, so the player stored here is the one setupAnim/setupRotations draw.
 * Non-player avatars (mannequins) get null, so ParCool leaves them alone.
 */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRenderStateExtractorMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void parcool$keepPlayer(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        ((IAvatarRenderStateEntity) state).parcool$setPlayer(entity instanceof AbstractClientPlayer player ? player : null);
    }
}
