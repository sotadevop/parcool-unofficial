package com.alrex.parcool.fabric.compat.event;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Supplier;

public abstract class EntityRenderersEvent extends Event {
    public static class RegisterLayerDefinitions extends EntityRenderersEvent {
        public static final RegisterLayerDefinitions INSTANCE = new RegisterLayerDefinitions();

        public void registerLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> supplier) {
            ModelLayerRegistry.registerModelLayer(layer, supplier::get);
        }
    }
}
