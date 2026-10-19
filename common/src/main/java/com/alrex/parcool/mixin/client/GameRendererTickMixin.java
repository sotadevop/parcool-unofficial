package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.event.CompatEvents;
import com.alrex.parcool.client.action.ClientActionProcessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces {@code RenderFrameEvent.Pre}, which has no Architectury counterpart.
 * <p>
 * {@code GameRenderer#renderLevel} is the last point before entity rendering begins, which is
 * exactly where ParCool's per-frame action rotation ({@code Action#onRenderTick}, used to drive
 * {@code yBodyRot} / {@code yRot} / head yaw) has to run. NeoForge fired the event once per frame
 * from Minecraft's tick loop; this hook is one frame tighter, which is harmless because the
 * consumers only write rotation fields that are read later, during entity rendering.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererTickMixin {

    // 26.3 split frame rendering into extract(DeltaTracker, boolean) and a no-argument render();
    // extract is the per-frame entry that still receives the frame's DeltaTracker.
    @Inject(method = "extract(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("HEAD"))
    private void parcool$onRenderFrame(DeltaTracker partialTick, boolean renderLevel, CallbackInfo ci) {
        ClientActionProcessor.onRenderFrame(new CompatEvents.RenderFrameEvent.Pre(partialTick));
    }
}
