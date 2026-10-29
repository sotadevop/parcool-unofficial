package com.alrex.parcool.fabric.compat.event;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerEvent extends LivingEvent {
    protected PlayerEvent(Player player) {
        super(player);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    /** Fired when a new player entity replaces an old one (respawn or leaving the End). */
    public static class Clone extends PlayerEvent {
        private final Player original;
        private final boolean wasDeath;

        public Clone(Player newPlayer, Player original, boolean wasDeath) {
            super(newPlayer);
            this.original = original;
            this.wasDeath = wasDeath;
        }

        public Player getOriginal() {
            return original;
        }

        public boolean isWasDeath() {
            return wasDeath;
        }
    }

    public static class PlayerChangedDimensionEvent extends PlayerEvent {
        public PlayerChangedDimensionEvent(Player player) {
            super(player);
        }
    }
}
