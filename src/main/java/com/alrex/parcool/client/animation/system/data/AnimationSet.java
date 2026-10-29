package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimationPhase;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public record AnimationSet(
        Identifier name,
        @Nullable AnimationComponentGroup introAnimation,
        AnimationComponentGroup mainAnimation,
        @Nullable AnimationComponentGroup outroAnimation,
        @Nullable Supplier<IBlendingFactor> blendingFactorSupplier,
        int fadeInDuration,
        int fadeOutDuration,
        int priority,
        float blendFactorInFirstPersonView,
        @Nullable
        Vec3f cameraAnimationScales
) {
    @Nullable
    AnimationComponentGroup getAnimation(AnimationPhase phase) {
        return switch (phase) {
            case INTRO -> introAnimation;
            case MAIN -> mainAnimation;
            case OUTRO -> outroAnimation;
            case END -> null;
        };
    }
}
