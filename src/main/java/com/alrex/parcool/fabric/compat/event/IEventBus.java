package com.alrex.parcool.fabric.compat.event;

/**
 * Stands in for the NeoForge mod event bus that upstream passes to its {@code register(IEventBus)}
 * methods. On Fabric registration happens immediately, so the bus is only a token; {@link #post}
 * forwards to {@link ParCoolEventBus}.
 */
public interface IEventBus {
    IEventBus INSTANCE = new IEventBus() {
    };

    default <T extends Event> T post(T event) {
        return ParCoolEventBus.post(event);
    }
}
