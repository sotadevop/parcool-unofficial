package com.alrex.parcool.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


@Environment(EnvType.CLIENT)
public class GuiRenderUtil {
    public static void enableScissorTestInGuiCoordinate(double x, double y, double width, double height) {
        var window = Minecraft.getInstance().getWindow();
        var guiScale = window.getGuiScale();

        RenderSystem.enableScissor(
                (int) (guiScale * x),
                window.getHeight() - (int) (guiScale * (y + height)),
                Mth.ceil(width * guiScale),
                Mth.ceil(height * guiScale)
        );
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
        graphics.innerBlit(
                sprite.atlasLocation(),
                x, x + halfWidth,
                y, y + halfHeight,
                0 /*z pos*/,
                sprite.getU0(), midU0,
                sprite.getV0(), midV0
        );
        graphics.innerBlit(
                sprite.atlasLocation(),
                x + halfWidth, x + width,
                y, y + halfHeight,
                0,
                midU1, sprite.getU1(),
                sprite.getV0(), midV0
        );
        graphics.innerBlit(
                sprite.atlasLocation(),
                x, x + halfWidth,
                y + halfHeight, y + height,
                0,
                sprite.getU0(), midU0,
                midV1, sprite.getV1()
        );
        graphics.innerBlit(
                sprite.atlasLocation(),
                x + halfWidth, x + width,
                y + halfHeight, y + height,
                0,
                midU1, sprite.getU1(),
                midV1, sprite.getV1()
        );
    }
}
