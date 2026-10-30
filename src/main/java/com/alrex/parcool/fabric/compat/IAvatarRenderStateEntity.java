package com.alrex.parcool.fabric.compat;

import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Added to AvatarRenderState by AvatarRenderStateEntityMixin. 26.x renderers only see a render
 * state while drawing, but ParCool's animator is keyed on the player, so the player travels with the
 * state. Lives outside the mixin package because ordinary code may not reference classes in it.
 */
public interface IAvatarRenderStateEntity {
    AbstractClientPlayer parcool$getPlayer();

    void parcool$setPlayer(AbstractClientPlayer player);
}
