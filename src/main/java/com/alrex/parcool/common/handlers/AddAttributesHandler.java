package com.alrex.parcool.common.handlers;


import com.alrex.parcool.api.ParCoolAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import com.alrex.parcool.fabric.compat.event.EntityAttributeModificationEvent;

public class AddAttributesHandler {
    public static void onAddAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityTypes.PLAYER, ParCoolAttributes.MAX_STAMINA.getDelegate());
        event.add(EntityTypes.PLAYER, ParCoolAttributes.STAMINA_RECOVERY.getDelegate());
        event.add(EntityTypes.PLAYER, ParCoolAttributes.BREAKFALL_DAMAGE_REDUCTION.getDelegate());
        event.add(EntityTypes.PLAYER, ParCoolAttributes.FAST_RUN_SPEED.getDelegate());
        event.add(EntityTypes.PLAYER, ParCoolAttributes.FAST_SWIM_SPEED.getDelegate());
        event.add(EntityTypes.PLAYER, ParCoolAttributes.SLIDE_DOWN_DECELERATION.getDelegate());
        event.add(EntityTypes.PLAYER, ParCoolAttributes.HORIZONTAL_WALL_RUN_DURATION.getDelegate());
    }
}
