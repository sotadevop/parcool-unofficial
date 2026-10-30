package com.alrex.parcool.client.gui.components;

import net.minecraft.client.input.MouseButtonInfo;

import net.minecraft.client.renderer.RenderPipelines;
import com.alrex.parcool.ParCool;
import com.alrex.parcool.util.ColorUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import javax.annotation.Nonnull;

public class TexturedPanel extends AbstractWidget {
    public static class Textures {
        public static final Identifier SETTING_TOP = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/setting/top.png");
        public static final Identifier SETTING_SURFACE = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/setting/surface.png");
        public static final Identifier SETTING_CARD = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/setting/card.png");
        public static final Identifier SETTING_BACKGROUND = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/setting/background.png");
        public static final Identifier SKILLTREE_TOP = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/skilltree/top.png");
        public static final Identifier SKILLTREE_SURFACE = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/skilltree/surface.png");
        public static final Identifier SKILLTREE_BACKGROUND = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/skilltree/background.png");
    }

    private final Identifier texLocation;
    private final int texWidth;
    private final int texHeight;
    private int borderColor;

    public TexturedPanel(int x, int y, int width, int height, Identifier texLocation) {
        this(x, y, width, height, texLocation, 16, 16);
    }

    public TexturedPanel(int x, int y, int width, int height, Identifier texLocation, int texWidth, int texHeight) {
        super(x, y, width, height, Component.empty());
        this.texLocation = texLocation;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    public TexturedPanel withBorder(int color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void extractWidgetRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        if (ColorUtil.alphaOf(borderColor) != 0) {
            graphics.fill(getX(), getY() - 1, getX() + width, getY(), borderColor);
            graphics.fill(getX(), getY() + height, getX() + width, getY() + height + 1, borderColor);
            graphics.fill(getX() - 1, getY(), getX(), getY() + height, borderColor);
            graphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, borderColor);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, texLocation, getX(), getY(), 0, 0, width, height, texWidth, texHeight);
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return false;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
