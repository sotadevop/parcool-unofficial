package com.alrex.parcool.client.textures;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.fabric.compat.event.RegisterClientReloadListenersEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * Upstream stitched three atlases of its own (TextureAtlasHolder, removed in 26.x). The vanilla GUI
 * atlas already collects textures/gui/sprites from every namespace, so ParCool's GUI sprites, action
 * icons (gui/sprites/parcool_actions) and guide images (gui/sprites/parcool_guide) are taken from it.
 */
@Environment(EnvType.CLIENT)
public class ParCoolTextures {
    private ParCoolTextures() {
    }

    public static void init(RegisterClientReloadListenersEvent event) {
        // Nothing to register: the GUI atlas is reloaded by vanilla.
    }

    private static TextureAtlas guiAtlas() {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI);
    }

    public static TextureAtlasSprite guiSprite(Identifier spriteId) {
        return guiAtlas().getSprite(spriteId);
    }

    public static TextureAtlasSprite guideResourceSprite(Identifier spriteId) {
        return guiAtlas().getSprite(spriteId.withPrefix("parcool_guide/"));
    }

    public static TextureAtlasSprite action(ActionEntry<?> entry) {
        return guiAtlas().getSprite(entry.id().withPrefix("parcool_actions/"));
    }
}
