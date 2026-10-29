package com.alrex.parcool.mixin.fabric.client;

import com.alrex.parcool.fabric.FabricClientEventBridge;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's RenderFrameEvent.Pre: once per frame, before the frame is extracted. */
@Mixin(GameRenderer.class)
public abstract class RenderFrameEventMixin {
    @Inject(method = "extract(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("HEAD"))
    private void parcool$renderFrameEvent(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        FabricClientEventBridge.fireRenderFrame(deltaTracker);
    }
}
