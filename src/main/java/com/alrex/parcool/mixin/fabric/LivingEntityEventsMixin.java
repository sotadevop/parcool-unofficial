package com.alrex.parcool.mixin.fabric;

import com.alrex.parcool.fabric.FabricEventBridge;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires the living-entity events NeoForge patches into {@link LivingEntity} (see FabricEventBridge). */
@Mixin(LivingEntity.class)
public abstract class LivingEntityEventsMixin {
    /** NeoForge's LivingFallEvent: may change distance and multiplier, or cancel the fall damage. */
    @WrapMethod(method = "causeFallDamage")
    private boolean parcool$fallEvent(double fallDistance, float damageModifier, DamageSource source, Operation<Boolean> original) {
        var event = FabricEventBridge.fireFall((LivingEntity) (Object) this, (float) fallDistance, damageModifier);
        if (event.isCanceled()) return false;
        return original.call((double) event.getDistance(), event.getDamageMultiplier(), source);
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void parcool$jumpEvent(CallbackInfo ci) {
        FabricEventBridge.fireJump((LivingEntity) (Object) this);
    }

    @ModifyReturnValue(method = "getVisibilityPercent", at = @At("RETURN"))
    private double parcool$visibilityEvent(double visibility, Entity lookingEntity) {
        return FabricEventBridge.fireVisibility((LivingEntity) (Object) this, visibility);
    }

    /** NeoForge's LivingDamageEvent.Pre: the damage that is about to be applied can be changed. */
    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true)
    private float parcool$damageEvent(float damage, ServerLevel level, DamageSource source) {
        return FabricEventBridge.fireDamagePre((LivingEntity) (Object) this, source, damage);
    }
}
