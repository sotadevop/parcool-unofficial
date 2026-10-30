package com.alrex.parcool.mixin.client;

import com.alrex.parcool.fabric.compat.IAvatarRenderStateEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/** Carries the rendered player on its render state (written in AvatarRenderStateExtractorMixin). */
@Mixin(AvatarRenderState.class)
public abstract class AvatarRenderStateEntityMixin implements IAvatarRenderStateEntity {
    @Unique
    private AbstractClientPlayer parcool$player;

    @Override
    public AbstractClientPlayer parcool$getPlayer() {
        return parcool$player;
    }

    @Override
    public void parcool$setPlayer(AbstractClientPlayer player) {
        parcool$player = player;
    }
}
