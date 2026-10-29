package com.alrex.parcool.fabric.compat.event;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

/** Registers a server data (datapack) reload listener. */
public class AddReloadListenerEvent extends Event {
    public static final AddReloadListenerEvent INSTANCE = new AddReloadListenerEvent();

    public void addListener(PreparableReloadListener listener) {
        ReloadListeners.register(PackType.SERVER_DATA, listener);
    }
}
