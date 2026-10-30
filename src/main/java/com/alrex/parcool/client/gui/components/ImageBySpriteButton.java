package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.gui.GuiRenderUtil;

import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImageBySpriteButton extends AbstractButton {
    private final int txtColor;
    private final Font font;
    @Nullable
    private final Runnable runnable;
    private final Identifier texLocation;
    private TextureAtlasSprite sprite;

    public ImageBySpriteButton(Font font, int x, int y, int width, int height, Component text, int txtColor, Identifier texLocation, TextureAtlasSprite sprite, @Nullable Runnable onPressListener) {
        super(x, y, width, height, text);
        this.font = font;
        this.txtColor = txtColor;
        this.runnable = onPressListener;
        this.texLocation = texLocation;
        this.sprite = sprite;
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void extractContents(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        int color;
        if (active) {
            color = isHovered ? GuiRenderUtil.tint(0.85f, 0.85f, 0.85f, 0.85f) : ~0;
        } else {
            color = GuiRenderUtil.tint(0.5f, 0.5f, 0.5f, 0.5f);
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height, color);
        var message = getMessage();
        graphics.text(font, message, (int) (getX() + (width - font.width(message)) / 2f), (int) (1 + getY() + (height - font.lineHeight) / 2f), txtColor, false);
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (runnable != null) runnable.run();
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
