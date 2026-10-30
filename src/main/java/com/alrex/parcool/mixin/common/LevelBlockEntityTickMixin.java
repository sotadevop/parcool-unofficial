package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.zipline.ILoadedZiplineHolderProvider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Clears the client's list of live ziplines right before block entities tick; each zipline hook then
 * registers its ropes again. Upstream hooked ClientLevel#tickEntities at its tickBlockEntities call,
 * which 26.x moved out of that method. Only the client level implements ILoadedZiplineHolderProvider.
 */
@Mixin(Level.class)
public abstract class LevelBlockEntityTickMixin {
    @Inject(method = "tickBlockEntities", at = @At("HEAD"))
    private void parcool$clearLoadedZiplines(CallbackInfo ci) {
        if (this instanceof ILoadedZiplineHolderProvider provider) {
            provider.getZiplineHolder().clear();
        }
    }
}
