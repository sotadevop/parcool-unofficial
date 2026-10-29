package com.alrex.parcool.registrar;

import com.alrex.parcool.common.network.NetworkRegistrar;
import com.alrex.parcool.common.resource.skilltree.SkilltreeResourceManager;
import com.alrex.parcool.fabric.FabricEventBridge;
import com.alrex.parcool.fabric.MixinAudit;
import com.alrex.parcool.fabric.compat.event.AddReloadListenerEvent;
import com.alrex.parcool.fabric.compat.network.PacketDistributor;
import com.alrex.parcool.fabric.compat.network.RegisterPayloadHandlersEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Fabric port: upstream subscribed its common handlers on the NeoForge buses here. On Fabric the
 * game event handlers are reached through {@link FabricEventBridge}; brewing recipes are data files
 * ({@code data/parcool/recipe/brewing}) because 26.3 made brewing data-driven.
 */
public class CommonRegistrar {
    public static void init() {
        PacketDistributor.init();
        NetworkRegistrar.registerPackets(RegisterPayloadHandlersEvent.INSTANCE);
        SkilltreeResourceManager.register(AddReloadListenerEvent.INSTANCE);
        FabricEventBridge.init();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (server.isDedicatedServer()) MixinAudit.runIfRequested("server");
        });
    }
}
