package com.alrex.parcool.mixin.fabric;

import com.alrex.parcool.fabric.FabricEventBridge;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** NeoForge's EntityEvent.Size: handlers can replace the size computed for the current pose. */
@Mixin(Entity.class)
public abstract class EntitySizeEventMixin {
    @ModifyExpressionValue(method = "refreshDimensions", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"))
    private EntityDimensions parcool$sizeEvent(EntityDimensions size) {
        return FabricEventBridge.fireSize((Entity) (Object) this, size);
    }
}
