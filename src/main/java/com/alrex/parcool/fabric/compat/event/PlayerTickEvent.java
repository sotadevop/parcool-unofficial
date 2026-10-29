package com.alrex.parcool.fabric.compat.event;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerTickEvent extends PlayerEvent {
    protected PlayerTickEvent(Player player) {
        super(player);
    }

    /** Fired at the end of {@code Player#tick}, on both logical sides. */
    public static class Post extends PlayerTickEvent {
        public Post(Player player) {
            super(player);
        }
    }
}
