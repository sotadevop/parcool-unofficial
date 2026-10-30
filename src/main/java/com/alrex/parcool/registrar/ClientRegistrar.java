package com.alrex.parcool.registrar;

import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.event.RegisterAnimationEntryEvent;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.resource.AnimationResourceManager;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.fabric.FabricClientEventBridge;
import com.alrex.parcool.fabric.MixinAudit;
import com.alrex.parcool.fabric.compat.event.EntityRenderersEvent;
import com.alrex.parcool.fabric.compat.event.ParCoolEventBus;
import com.alrex.parcool.fabric.compat.event.RegisterClientReloadListenersEvent;
import com.alrex.parcool.fabric.compat.event.RegisterGuiLayersEvent;
import com.alrex.parcool.fabric.compat.event.RegisterKeyMappingsEvent;
import com.alrex.parcool.fabric.compat.network.ClientPayloads;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.neoforged.fml.config.ModConfig;

/**
 * Fabric port: upstream's client mod-loading listeners, run in the same order at client init,
 * followed by what upstream did in {@code FMLClientSetupEvent}.
 */
@Environment(EnvType.CLIENT)
public class ClientRegistrar {
    public static void init() {
        ParCoolKeyBinds.registerAll(RegisterKeyMappingsEvent.INSTANCE);
        HUDRegistry.getInstance().onSetup(RegisterGuiLayersEvent.INSTANCE);
        ParCoolModelLayers.register(EntityRenderersEvent.RegisterLayerDefinitions.INSTANCE);
        ParCoolTextures.init(RegisterClientReloadListenersEvent.INSTANCE);
        AnimationResourceManager.register(RegisterClientReloadListenersEvent.INSTANCE);
        GuideResourceManager.register(RegisterClientReloadListenersEvent.INSTANCE);

        var registerAnimationEntryEvent = new RegisterAnimationEntryEvent();
        AnimationRegistries.register(registerAnimationEntryEvent);
        ParCoolEventBus.post(registerAnimationEntryEvent);
        registerAnimationEntryEvent.finish();
        AnimationSystemConfig.init(AnimationSets.getInstance());
        ConfigRegistry.INSTANCE.register(com.alrex.parcool.ParCool.MOD_ID, ModConfig.Type.CLIENT,
                AnimationSystemConfig.getInstance().getBuiltConfig(), "parcool-animation.toml");

        ClientPayloads.registerReceivers();
        FabricClientEventBridge.init();

        // Upstream's FMLClientSetupEvent and FMLLoadCompleteEvent work.
        Renderers.register();
        ParCoolItems.registerColors();
        AnimationSets.getInstance().freeze();
        AdditionalMods.initInClient();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> MixinAudit.runIfRequested("client"));
    }
}
