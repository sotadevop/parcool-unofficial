package com.alrex.parcool.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;

@Environment(EnvType.CLIENT)
public class ImageWidget extends AbstractWidget {
    private final int texX;
    private final int texY;
    private final int texWidth;
    private final int texHeight;
    private final Identifier textureLocation;

    public ImageWidget(int x, int y, int width, int height, Identifier textureLocation, int texX, int texY, int texWidth, int texHeight) {
        super(x, y, width, height, Component.empty());
        this.textureLocation = textureLocation;
        this.texX = texX;
        this.texY = texY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        graphics.blit(textureLocation, getX(), getY(), width, height, texX, texY, texWidth, texHeight, 256, 256);
    }

    @Override
    protected boolean isValidClickButton(int click) {
        return false;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
