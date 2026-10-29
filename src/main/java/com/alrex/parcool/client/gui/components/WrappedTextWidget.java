package com.alrex.parcool.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WrappedTextWidget extends TextWidget {
    private List<FormattedCharSequence> sequences;

    public WrappedTextWidget(Font font, int x, int y, int width, Component message, HorizontalAlignment textAlignment, int txtColor) {
        super(font, x, y, width, message, textAlignment, txtColor);
        this.sequences = font.split(message, width);
        setHeight(font.lineHeight * sequences.size());
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.sequences = font.split(getMessage(), width);
        setHeight(font.lineHeight * sequences.size());
    }

    @Override
    public void setMessage(@Nonnull Component message) {
        super.setMessage(message);
        this.sequences = font.split(message, width);
        setHeight(font.lineHeight * sequences.size());
    }

    public WrappedTextWidget withShadow(boolean value) {
        this.shadow = value;
        return this;
    }

    @Override
    public void extractWidgetRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        var iterator = sequences.iterator();
        int textY = getY();
        while (iterator.hasNext()) {
            var text = iterator.next();
            var xOffset = switch (alignment) {
                case START -> getX();
                case END -> getX() + width - font.width(text);
                case CENTER -> getX() + (width - font.width(text)) / 2f;
            };
            graphics.text(font, text, (int) xOffset, textY, txtColor, shadow);
            textY += font.lineHeight;
        }
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        super.updateNarration(narrationElementOutput);
    }
}
