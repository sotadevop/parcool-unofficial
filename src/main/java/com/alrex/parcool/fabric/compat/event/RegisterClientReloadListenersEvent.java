package com.alrex.parcool.fabric.compat.event;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

/** Registers a client resource (resource pack) reload listener. */
public class RegisterClientReloadListenersEvent extends Event {
    public static final RegisterClientReloadListenersEvent INSTANCE = new RegisterClientReloadListenersEvent();

    public void registerReloadListener(PreparableReloadListener listener) {
        ReloadListeners.register(PackType.CLIENT_RESOURCES, listener);
    }
}
