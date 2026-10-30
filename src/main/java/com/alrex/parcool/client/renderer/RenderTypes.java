package com.alrex.parcool.client.renderer;

import com.alrex.parcool.client.renderer.blockentity.ZiplineHookRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Fabric port: upstream built two custom render types (textured, lightmapped quads). 26.x replaced
 * RenderStateShard/CompositeState with render pipelines; vanilla's entity render types cover both
 * cases, so the renderers emit the entity vertex format (with overlay and normal) instead.
 */
public class RenderTypes {
    /** Zipline rope: opaque, back faces culled. */
    public static final RenderType ZIPLINE_3D = net.minecraft.client.renderer.rendertype.RenderTypes.entitySolid(ZiplineHookRenderer.TEXTURE_LOCATION);
    /** Grapple rope: alpha-tested, visible from both sides. */
    public static final RenderType GRAPPLE_ROPE = net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(GrappleRopeRenderer.TEXTURE_LOCATION);
}
