package com.alrex.parcool.client.gui.components;

import net.minecraft.client.input.MouseButtonInfo;

import net.minecraft.client.renderer.RenderPipelines;
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
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height);
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return false;
    }

    public void setImage(@Nullable TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
