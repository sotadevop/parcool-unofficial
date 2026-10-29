package com.alrex.parcool.client.animation.system.event;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.AnimationProcessor;
import com.alrex.parcool.client.animation.system.ModelTransform;
import net.minecraft.client.player.AbstractClientPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.Event;
import com.alrex.parcool.fabric.compat.event.ICancellableEvent;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class AnimationUpdateEvent extends Event {
    private final AbstractClientPlayer player;
    private final AnimationProcessor processor;

    private AnimationUpdateEvent(AbstractClientPlayer player, AnimationProcessor processor) {
        this.player = player;
        this.processor = processor;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public AnimationProcessor getProcessor() {
        return processor;
    }

    public static class Pre extends AnimationUpdateEvent implements ICancellableEvent {
        private boolean stopAnimation;

        public Pre(AbstractClientPlayer player, AnimationProcessor processor) {
            super(player, processor);
        }

        public boolean isAnimationStopped() {
            return stopAnimation;
        }

        public void stop() {
            this.stopAnimation = true;
            setCanceled(true);
        }
    }

    public static class Post extends AnimationUpdateEvent {
        private final ModelTransform transformation;

        public Post(AbstractClientPlayer player, AnimationProcessor processor, ModelTransform transformation) {
            super(player, processor);
            this.transformation = transformation;
        }

        @Nullable
        public ModelTransform getModelTransform() {
            return transformation;
        }

        public void stop(AnimatableModelPart part) {
            var transform = transformation.transforms();
            if (transform == null) return;
            transform.remove(part);
        }
    }
}

