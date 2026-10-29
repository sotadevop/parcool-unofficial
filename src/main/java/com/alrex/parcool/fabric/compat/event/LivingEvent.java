package com.alrex.parcool.fabric.compat.event;

import net.minecraft.world.entity.LivingEntity;

public abstract class LivingEvent extends Event {
    private final LivingEntity entity;

    protected LivingEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    /** Fired after a living entity jumped from the ground. */
    public static class LivingJumpEvent extends LivingEvent {
        public LivingJumpEvent(LivingEntity entity) {
            super(entity);
        }
    }

    /** Lets handlers scale how visible an entity is to mobs (for example while hiding). */
    public static class LivingVisibilityEvent extends LivingEvent {
        private double visibilityModifier;

        public LivingVisibilityEvent(LivingEntity entity, double visibilityModifier) {
            super(entity);
            this.visibilityModifier = visibilityModifier;
        }

        public void modifyVisibility(double modifier) {
            visibilityModifier *= modifier;
        }

        public double getVisibilityModifier() {
            return visibilityModifier;
        }
    }
}
