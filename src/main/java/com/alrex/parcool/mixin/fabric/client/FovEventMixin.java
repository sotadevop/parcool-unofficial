package com.alrex.parcool.mixin.fabric.client;

import com.alrex.parcool.fabric.FabricClientEventBridge;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** NeoForge's ComputeFovModifierEvent. */
@Mixin(AbstractClientPlayer.class)
public abstract class FovEventMixin {
    @ModifyReturnValue(method = "getFieldOfViewModifier", at = @At("RETURN"))
    private float parcool$fovEvent(float fov) {
        return FabricClientEventBridge.fireFov((AbstractClientPlayer) (Object) this, fov);
    }
}
