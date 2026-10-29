package com.alrex.parcool.fabric.compat.event;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

import java.util.Map;

/** NeoForge's {@code VanillaGuiLayers} ids that ParCool uses, mapped to Fabric's ids. */
final class VanillaLayers {
    private static final Map<String, Identifier> NEOFORGE_TO_FABRIC = Map.of(
            "minecraft:food_level", VanillaHudElements.FOOD_BAR,
            "minecraft:player_health", VanillaHudElements.HEALTH_BAR,
            "minecraft:air_level", VanillaHudElements.AIR_BAR,
            "minecraft:armor_level", VanillaHudElements.ARMOR_BAR,
            "minecraft:hotbar", VanillaHudElements.HOTBAR,
            "minecraft:crosshair", VanillaHudElements.CROSSHAIR
    );

    private VanillaLayers() {
    }

    static Identifier toFabric(Identifier neoForgeId) {
        var id = NEOFORGE_TO_FABRIC.get(neoForgeId.toString());
        if (id == null) throw new IllegalArgumentException("No Fabric HUD element for " + neoForgeId);
        return id;
    }
}
