package com.alrex.parcool.mixin.fabric.client;

import com.alrex.parcool.fabric.FabricClientEventBridge;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NeoForge's MovementInputUpdateEvent: fired right after the movement keys were read. */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerInputEventMixin {
    @Shadow
    public ClientInput input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;tick()V", shift = At.Shift.AFTER))
    private void parcool$movementInputEvent(CallbackInfo ci) {
        FabricClientEventBridge.fireMovementInput((LocalPlayer) (Object) this, input);
    }
}
