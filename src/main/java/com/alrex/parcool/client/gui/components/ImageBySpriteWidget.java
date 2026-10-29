package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class ImageBySpriteWidget extends AbstractWidget {
    @Nullable
    private TextureAtlasSprite sprite;
    private final Identifier texLocation;

    public ImageBySpriteWidget(int x, int y, int width, int height, Identifier texLocation, @Nullable TextureAtlasSprite sprite) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
        this.texLocation = texLocation;
    }

    @Override
    public void extractWidgetRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        if (sprite == null) return;
        RenderSystem.setShaderTexture(0, texLocation);
        graphics.blit(getX(), getY(), 0, width, height, sprite);
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    public void setImage(@Nullable TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
