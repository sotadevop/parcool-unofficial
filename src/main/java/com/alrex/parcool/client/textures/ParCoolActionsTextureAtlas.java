package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ParCoolActionsTextureAtlas extends TextureAtlasHolder {
    public static final Identifier TEXTURE_LOCATION = ParCool.resourceLocation("textures/atlas/action_icons.png");

    protected ParCoolActionsTextureAtlas(TextureManager manager) {
        super(manager, TEXTURE_LOCATION, ParCool.resourceLocation("parcool_actions"));
    }

    public TextureAtlasSprite getSprite(ActionEntry<?> entry) {
        return getSprite(entry.id());
    }
}
