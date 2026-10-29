package com.alrex.parcool.fabric.compat.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public abstract class LivingDamageEvent extends LivingEvent {
    protected LivingDamageEvent(LivingEntity entity) {
        super(entity);
    }

    /** Fired before damage is applied to the entity's health; the damage can be changed. */
    public static class Pre extends LivingDamageEvent {
        private final DamageSource source;
        private float newDamage;

        public Pre(LivingEntity entity, DamageSource source, float damage) {
            super(entity);
            this.source = source;
            this.newDamage = damage;
        }

        public DamageSource getSource() {
            return source;
        }

        public float getNewDamage() {
            return newDamage;
        }

        public void setNewDamage(float newDamage) {
            this.newDamage = newDamage;
        }
    }
}
