package com.alrex.parcool.client.gui.components;

import net.minecraft.client.renderer.RenderPipelines;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

public class ExperienceProgressView extends AbstractWidget {
    private final Font font;
    private float progress;

    public ExperienceProgressView(Font font, int x, int y, Component component) {
        super(x, y, 66, 14, component);
        this.font = font;
    }

    @Override
    public void extractWidgetRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.EXPERIENCE_BOX), getX(), getY(), width, height);
        var sprite = ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.EXPERIENCE_BOX_OVERLAY);
        graphics.blit(sprite.atlasLocation(), getX(), getY(), (int) (getX() + sprite.contents().width() * progress), getY() + sprite.contents().height(), sprite.getU0(), sprite.getU(progress), sprite.getV0(), sprite.getV1());
        var message = getMessage();
        var messageWidth = font.width(message);
        graphics.text(font, message, getX() + width - 3 - messageWidth, getY() + 1 + (height - font.lineHeight) / 2, ~0, true);
    }

    public void setProgress(float progress) {
        this.progress = Mth.clamp(progress, 0, 1);
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
