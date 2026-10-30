package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingRendererMixin {

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
    protected void onShouldShowName(LivingEntity entity, double distance, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player) {
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            if (parkourability.getBehaviorEnforcer().noShowNameMarks.enforce()) {
                cir.setReturnValue(false);
            }
        }
    }

}
