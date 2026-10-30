package com.alrex.parcool.client.gui.components;

import net.minecraft.client.input.MouseButtonInfo;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import net.minecraft.client.input.MouseButtonEvent;

public class ClickableTextWidget extends TextWidget {
    private final int hoveredTxtColor;
    private final Runnable pressListener;

    public ClickableTextWidget(Font font, int x, int y, int width, Component message, HorizontalAlignment alignment, int txtColor, int hoveredTxtColor, Runnable pressListener) {
        super(font, x, y, width, message, alignment, txtColor);
        this.hoveredTxtColor = hoveredTxtColor;
        this.pressListener = pressListener;
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return true;
    }

    @Override
    public void extractWidgetRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        var xOffset = switch (alignment) {
            case START -> getX();
            case END -> getX() + width - messageWidth;
            case CENTER -> getX() + (width - messageWidth) / 2f;
        };
        graphics.text(font, getMessage(), (int) xOffset, getY(), isHovered ? hoveredTxtColor : txtColor, shadow);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (pressListener != null) pressListener.run();
    }
}
