package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player implements IPlayerAnimatorHolder {
    public AbstractClientPlayerMixin(Level level, GameProfile profile) {
        super(level, profile);
    }

    @Unique
    private PlayerAnimator parcool$animator;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(ClientLevel p_250460_, GameProfile p_249912_, CallbackInfo ci) {
        parcool$animator = new PlayerAnimator((AbstractClientPlayer) (Player) this);
    }

    @Override
    public PlayerAnimator getParCoolPlayerAnimator() {
        return parcool$animator;
    }

}
