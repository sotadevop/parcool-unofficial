package com.alrex.parcool.fabric.compat.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Delivers ParCool's own API events ({@code ParCoolActionEvent}, {@code AnimationUpdateEvent},
 * {@code RenderParCoolHUDEvent}, ...), which upstream posts on {@code NeoForge.EVENT_BUS} for
 * add-on mods. A Fabric add-on subscribes with {@link #addListener}; with no listeners,
 * {@link #post} just returns the event unchanged.
 *
 * <p>Listeners are matched by the exact event class (no supertype dispatch), which is all ParCool's
 * events need: every concrete phase (for example {@code Start.Pre}) is its own class.
 */
public final class ParCoolEventBus {
    private static final Map<Class<?>, List<Consumer<Object>>> LISTENERS = new ConcurrentHashMap<>();

    private ParCoolEventBus() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> void addListener(Class<T> type, Consumer<? super T> listener) {
        LISTENERS.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add((Consumer<Object>) listener);
    }

    public static <T extends Event> T post(T event) {
        var listeners = LISTENERS.get(event.getClass());
        if (listeners != null) {
            for (var listener : listeners) listener.accept(event);
        }
        return event;
    }
}
