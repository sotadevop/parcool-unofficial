package com.alrex.parcool.fabric.compat.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;

public abstract class EntityEvent extends Event {
    private final Entity entity;

    protected EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    /** Fired when an entity recomputes its size; handlers can replace the new size. */
    public static class Size extends EntityEvent {
        private EntityDimensions newSize;

        public Size(Entity entity, EntityDimensions newSize) {
            super(entity);
            this.newSize = newSize;
        }

        public EntityDimensions getNewSize() {
            return newSize;
        }

        public void setNewSize(EntityDimensions newSize) {
            this.newSize = newSize;
        }
    }
}
