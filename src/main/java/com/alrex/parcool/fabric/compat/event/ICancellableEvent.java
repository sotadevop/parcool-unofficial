package com.alrex.parcool.fabric.compat.event;

/** Same contract as NeoForge's: only events implementing this can be canceled. */
public interface ICancellableEvent {
    default void setCanceled(boolean canceled) {
        ((Event) this).canceled = canceled;
    }

    default boolean isCanceled() {
        return ((Event) this).canceled;
    }
}
