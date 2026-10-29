package com.alrex.parcool.mixin.fabric;

import com.alrex.parcool.fabric.FabricEventBridge;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEventsMixin {
    /** NeoForge's PlayerTickEvent.Post, on both logical sides. */
    @Inject(method = "tick", at = @At("TAIL"))
    private void parcool$playerTickEvent(CallbackInfo ci) {
        FabricEventBridge.firePlayerTick((Player) (Object) this);
    }

    /** NeoForge's EntityAttributeModificationEvent for players: ParCool's attributes join the defaults. */
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void parcool$playerAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        FabricEventBridge.firePlayerAttributes(cir.getReturnValue());
    }
}
