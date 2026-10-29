package com.alrex.parcool.fabric.compat.event;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

/**
 * Registers a HUD element. Upstream passes NeoForge's vanilla layer ids (for example
 * {@code minecraft:food_level}); they are translated to Fabric's {@code VanillaHudElements} ids.
 */
public class RegisterGuiLayersEvent extends Event {
    public static final RegisterGuiLayersEvent INSTANCE = new RegisterGuiLayersEvent();

    public void registerAbove(Identifier other, Identifier id, HudElement element) {
        HudElementRegistry.attachElementAfter(VanillaLayers.toFabric(other), id, element);
    }

    public void registerBelow(Identifier other, Identifier id, HudElement element) {
        HudElementRegistry.attachElementBefore(VanillaLayers.toFabric(other), id, element);
    }
}
