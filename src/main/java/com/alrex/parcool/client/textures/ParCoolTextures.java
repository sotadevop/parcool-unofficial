package com.alrex.parcool.client.textures;

import com.alrex.parcool.api.action.ActionEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.RegisterClientReloadListenersEvent;

@Environment(EnvType.CLIENT)
public class ParCoolTextures {
    private static ParCoolTextures INSTANCE;

    private final ParCoolActionsTextureAtlas actionsTextureAtlas;
    private final ParCoolGuiTextureAtlas guiTextureAtlas;
    private final ParCoolGuideImageTextureAtlas guideImageTextureAtlas;

    public static void init(RegisterClientReloadListenersEvent event) {
        INSTANCE = new ParCoolTextures(Minecraft.getInstance().getTextureManager());
        event.registerReloadListener(INSTANCE.actionsTextureAtlas);
        event.registerReloadListener(INSTANCE.guiTextureAtlas);
        event.registerReloadListener(INSTANCE.guideImageTextureAtlas);
    }

    public static ParCoolTextures instance() {
        return INSTANCE;
    }

    protected ParCoolTextures(TextureManager manager) {
        actionsTextureAtlas = new ParCoolActionsTextureAtlas(manager);
        guiTextureAtlas = new ParCoolGuiTextureAtlas(manager);
        guideImageTextureAtlas = new ParCoolGuideImageTextureAtlas(manager);
    }

    public static TextureAtlasSprite guiSprite(Identifier spriteId) {
        return INSTANCE.guiTextureAtlas.getSprite(spriteId);
    }

    public static TextureAtlasSprite guideResourceSprite(Identifier spriteId) {
        return INSTANCE.guideImageTextureAtlas.getSprite(spriteId);
    }

    public static TextureAtlasSprite action(ActionEntry<?> entry) {
        return INSTANCE.actionsTextureAtlas.getSprite(entry);
    }
}
