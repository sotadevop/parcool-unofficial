package com.alrex.parcool.fabric.compat.event;

import net.minecraft.world.InteractionHand;

public abstract class InputEvent extends Event {
    /**
     * Fired before the attack or use-item key performs its action. Canceling skips the action;
     * {@link #setSwingHand} controls whether the hand swing animation plays.
     */
    public static class InteractionKeyMappingTriggered extends InputEvent implements ICancellableEvent {
        public enum Kind { ATTACK, USE_ITEM, PICK_BLOCK }

        private final Kind kind;
        private final InteractionHand hand;
        private boolean swingHand = true;

        public InteractionKeyMappingTriggered(Kind kind, InteractionHand hand) {
            this.kind = kind;
            this.hand = hand;
        }

        public boolean isAttack() {
            return kind == Kind.ATTACK;
        }

        public boolean isUseItem() {
            return kind == Kind.USE_ITEM;
        }

        public boolean isPickBlock() {
            return kind == Kind.PICK_BLOCK;
        }

        public InteractionHand getHand() {
            return hand;
        }

        public void setSwingHand(boolean swingHand) {
            this.swingHand = swingHand;
        }

        public boolean shouldSwingHand() {
            return swingHand;
        }
    }
}
