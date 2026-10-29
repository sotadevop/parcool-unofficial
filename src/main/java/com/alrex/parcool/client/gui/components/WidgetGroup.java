package com.alrex.parcool.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;

public class WidgetGroup extends AbstractWidget {
    private final List<AbstractWidget> widgets;

    public WidgetGroup(int x, int y, int width, int height, List<AbstractWidget> widgets) {
        super(x, y, width, height, Component.empty());
        this.widgets = widgets;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void extractWidgetRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        var poseStack = graphics.pose();
        poseStack.pushMatrix();
        poseStack.translate((float) (getX()), (float) (getY()));
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            widget.extractRenderState(graphics, mouseX, mouseY, partial);
        }
        poseStack.popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int click = event.button();
        if (!visible) return false;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            if (widget.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(click, 0)), false)) return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (!visible) return;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            widget.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!visible) return false;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            if (widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int mouseX = event.key();
        int mouseY = event.keycode();
        int key = event.modifiers();
        if (!visible) return false;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            if (widget.keyPressed(new KeyEvent(mouseX, mouseY, key))) return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        int mouseX = event.key();
        int mouseY = event.keycode();
        int key = event.modifiers();
        if (!visible) return false;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            if (widget.keyReleased(new KeyEvent(mouseX, mouseY, key))) return true;
        }
        return false;
    }
}
