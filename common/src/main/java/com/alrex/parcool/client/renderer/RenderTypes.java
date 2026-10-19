package com.alrex.parcool.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * ParCool's own render types for the zipline rope.
 *
 * <h2>Why two pipelines are built instead of reusing vanilla's leash pipeline</h2>
 * Vanilla's leash pipeline draws {@code POSITION_COLOR_LIGHTMAP} as a {@code TRIANGLE_STRIP} with
 * culling off, while the zipline rope emits {@code QUADS} and has a culled and an unculled variant
 * (the {@code Enable3DRenderingForZipline} config switch).
 *
 * <p>26.3 moved rendering to Mojang's renderpearl library. The two pipelines below mirror how
 * {@link RenderPipelines} builds {@code LEASH} in 26.3 (same snippet, shaders, lightmap sampler,
 * vertex format, colour target and depth state); only the topology and culling differ.
 *
 * <p>{@code RenderPipelines#register} is private, but the map it fills is public, so the two
 * pipelines are registered the same way vanilla's are.
 */
public class RenderTypes {
    public static final RenderType ZIPLINE_3D;
    public static final RenderType ZIPLINE_2D;

    static {
        ZIPLINE_2D = RenderType.create("zipline2d",
                RenderSetup.builder(registerPipeline("parcool/zipline_no_cull", false))
                        .useLightmap()
                        .createRenderSetup());
        ZIPLINE_3D = RenderType.create("zipline3d",
                RenderSetup.builder(registerPipeline("parcool/zipline", true))
                        .useLightmap()
                        .createRenderSetup());
    }

    private static RenderPipeline registerPipeline(String location, boolean cull) {
        RenderPipeline pipeline = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
                .withLocation(location)
                .withVertexShader("core/rendertype_leash")
                .withFragmentShader("core/rendertype_leash")
                .withBindGroupLayout(BindGroupLayouts.SAMPLER2)
                .withCull(cull)
                .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP)
                .withPrimitiveTopology(PrimitiveTopology.QUADS)
                .withColorTargetState(ColorTargetState.DEFAULT)
                .withDepthStencilState(DepthStencilState.DEFAULT)
                .build();
        RenderPipelines.PIPELINES_BY_LOCATION.put(pipeline.getLocation(), pipeline);
        return pipeline;
    }
}
