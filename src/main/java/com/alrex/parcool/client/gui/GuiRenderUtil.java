package com.alrex.parcool.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


@Environment(EnvType.CLIENT)
public class GuiRenderUtil {
    // 26.x clips through the GUI graphics' scissor stack in GUI coordinates; the old version set the
    // GL scissor in window pixels directly.
    public static void enableScissorTestInGuiCoordinate(GuiGraphicsExtractor graphics, double x, double y, double width, double height) {
        graphics.enableScissor(Mth.floor(x), Mth.floor(y), Mth.ceil(x + width), Mth.ceil(y + height));
    }

    // RenderSystem#setShaderColor is gone in 26.x: the tint is now part of each draw call's colour.
    public static int tint(float r, float g, float b, float a) {
        return (Math.round(a * 255) & 0xFF) << 24 | (Math.round(r * 255) & 0xFF) << 16
                | (Math.round(g * 255) & 0xFF) << 8 | (Math.round(b * 255) & 0xFF);
    }

    public static int scaleAlpha(int argb, float factor) {
        int alpha = Math.round(((argb >>> 24) & 0xFF) * factor);
        return (alpha & 0xFF) << 24 | (argb & 0xFFFFFF);
    }

    public static void renderScaledGuiItem(GuiGraphicsExtractor graphics, ItemStack itemStack, float notScaledX, float notScaledY, int scaledX, int scaledY, float scale) {
        var poseStack = graphics.pose();
        poseStack.pushMatrix();
        {
            poseStack.translate((float) (notScaledX), (float) (notScaledY));
            poseStack.scale((float) (scale), (float) (scale));
            graphics.item(itemStack, scaledX, scaledY);
        }
        poseStack.popMatrix();
    }

    public static void renderExtendableSprite(GuiGraphicsExtractor graphics, TextureAtlasSprite sprite, int x, int y, int width, int height) {
        int halfWidth = width / 2, halfHeight = height / 2;
        float spriteWidth = sprite.contents().width(), spriteHeight = sprite.contents().height();

        float midU0 = sprite.getU(halfWidth / spriteWidth), midV0 = sprite.getV(halfHeight / spriteHeight);
        float midU1 = sprite.getU(1f - (width - halfWidth) / spriteWidth), midV1 = sprite.getV(1f - (height - halfHeight) / spriteHeight);
        graphics.blit(sprite.atlasLocation(), x, y, x + halfWidth, y + halfHeight, sprite.getU0(), midU0, sprite.getV0(), midV0);
        graphics.blit(sprite.atlasLocation(), x + halfWidth, y, x + width, y + halfHeight, midU1, sprite.getU1(), sprite.getV0(), midV0);
        graphics.blit(sprite.atlasLocation(), x, y + halfHeight, x + halfWidth, y + height, sprite.getU0(), midU0, midV1, sprite.getV1());
        graphics.blit(sprite.atlasLocation(), x + halfWidth, y + halfHeight, x + width, y + height, midU1, sprite.getU1(), midV1, sprite.getV1());
    }
}
