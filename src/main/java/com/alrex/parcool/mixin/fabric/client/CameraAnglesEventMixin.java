package com.alrex.parcool.mixin.fabric.client;

import com.alrex.parcool.fabric.FabricClientEventBridge;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge's ViewportEvent.ComputeCameraAngles. Vanilla's camera has no roll, so a roll is applied
 * by rotating the camera basis and its exported orientation (same approach as the 3.4 port).
 */
@Mixin(Camera.class)
public abstract class CameraAnglesEventMixin {
    // A shadowed field cannot have an initializer, so it is not final in Java; @Final tells Mixin the
    // target is final. Only the vectors' contents are changed, never the fields.
    @Shadow
    @Final
    private Vector3f up;

    @Shadow
    @Final
    private Vector3f left;

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Inject(method = "update(Lnet/minecraft/client/DeltaTracker;)V", at = @At("TAIL"))
    private void parcool$cameraAnglesEvent(DeltaTracker deltaTracker, CallbackInfo ci) {
        var self = (Camera) (Object) this;
        float yaw = self.yRot();
        float pitch = self.xRot();
        var event = FabricClientEventBridge.fireCameraAngles(self, self.getCameraEntityPartialTicks(deltaTracker), yaw, pitch, 0.0F);
        if (event.getYaw() != yaw || event.getPitch() != pitch) {
            setRotation(event.getYaw(), event.getPitch());
        }
        if (event.getRoll() != 0.0F) {
            var roll = new Quaternionf().rotateZ((float) Math.toRadians(event.getRoll()));
            up.rotate(roll);
            left.rotate(roll);
            // Keep the exported orientation in sync with the basis, or particles and entities facing
            // the camera keep the un-rolled orientation.
            self.rotation().mul(roll).normalize();
        }
    }
}
