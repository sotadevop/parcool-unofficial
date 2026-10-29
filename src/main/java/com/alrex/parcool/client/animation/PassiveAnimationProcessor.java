package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.PlayerTickEvent;

@Environment(EnvType.CLIENT)
public class PassiveAnimationProcessor {
    public void onTick(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (player instanceof IPlayerAnimatorHolder holder) {
            var animator = holder.getParCoolPlayerAnimator();
            if (!animator.isIdle()) return;
            if (player.getAbilities().flying) {
                animator.start(AnimationRegistries.get().animations().CREATIVE_FLY);
            }
        }
    }
}
