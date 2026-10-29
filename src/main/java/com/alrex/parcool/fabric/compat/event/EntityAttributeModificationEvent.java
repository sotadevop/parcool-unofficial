package com.alrex.parcool.fabric.compat.event;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

/**
 * Adds attributes to an entity type's defaults. Fired from the mixin on
 * {@code Player#createAttributes} with the player's builder; ParCool only adds player attributes.
 */
public class EntityAttributeModificationEvent extends Event {
    private final EntityType<?> type;
    private final AttributeSupplier.Builder builder;

    public EntityAttributeModificationEvent(EntityType<?> type, AttributeSupplier.Builder builder) {
        this.type = type;
        this.builder = builder;
    }

    public void add(EntityType<? extends LivingEntity> entityType, Holder<Attribute> attribute) {
        if (entityType == type) builder.add(attribute);
    }
}
