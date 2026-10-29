package com.alrex.parcool.fabric.compat.event;

import com.alrex.parcool.ParCool;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Locale;

/**
 * Fabric identifies every reload listener by an id, NeoForge does not. The id is derived from the
 * listener's class name plus a counter, which is stable because registration order is fixed.
 */
final class ReloadListeners {
    private static int counter;

    private ReloadListeners() {
    }

    static void register(PackType type, PreparableReloadListener listener) {
        var name = listener.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        var id = ParCool.resourceLocation((name.isEmpty() ? "listener" : name) + "_" + counter++);
        ResourceLoader.get(type).registerReloadListener(id, listener);
    }
}
