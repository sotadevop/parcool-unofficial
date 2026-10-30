package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import java.util.List;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;

@Environment(EnvType.CLIENT)
public class ScrollableWidgetGroup extends WidgetGroup {
    public enum ScrollType {
        HORIZONTAL, VERTICAL
    }

    private final int contentHeight;
    private final int contentWidth;
    private int scrollX;
    private int scrollY;
    private int scrollLimitX;
    private int scrollLimitY;
    private final ScrollType scrollType;

    public ScrollableWidgetGroup(int x, int y, int width, int height, ScrollType scrollType, List<AbstractWidget> widgets) {
        super(x, y, width, height, widgets);
        this.scrollType = scrollType;
        this.contentWidth = widgets.stream().map(it -> it.getX() + it.getWidth()).max(Integer::compareTo).orElse(1);
        this.contentHeight = widgets.stream().map(it -> it.getY() + it.getHeight()).max(Integer::compareTo).orElse(1);
        setWidth(width);
        setHeight(height);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.scrollLimitX = Math.max(contentWidth - width, 0);
    }

    @Override
    public void setHeight(int value) {
        super.setHeight(value);
        this.scrollLimitY = Math.max(contentHeight - height, 0);
    }

    @Override
    public void extractWidgetRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        GuiRenderUtil.enableScissorTestInGuiCoordinate(graphics, getX(), getY(), width, height);
        var poseStack = graphics.pose();
        poseStack.pushMatrix();
        {
            poseStack.translate((float) (-scrollX), (float) (-scrollY));
            super.extractWidgetRenderState(graphics, mouseX + scrollX, mouseY + scrollY, partial);
        }
        poseStack.popMatrix();
        graphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int click = event.button();
        return super.mouseClicked(new MouseButtonEvent(mouseX + scrollX, mouseY + scrollY, new MouseButtonInfo(click, 0)), false);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX + scrollX, mouseY + scrollY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        switch (scrollType) {
            case HORIZONTAL -> this.scrollX += (int) (scrollX * -16.);
            case VERTICAL -> this.scrollY += (int) (scrollY * -16.);
        }
        applyScrollLimits();
        return super.mouseScrolled(mouseX + scrollX, mouseY + scrollY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int mouseX = event.key();
        int mouseY = event.keycode();
        int key = event.modifiers();
        return super.keyPressed(new KeyEvent(mouseX + scrollX, mouseY + scrollY, key));
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        int mouseX = event.key();
        int mouseY = event.keycode();
        int key = event.modifiers();
        return super.keyReleased(new KeyEvent(mouseX + scrollX, mouseY + scrollY, key));
    }

    private void applyScrollLimits() {
        if (scrollX > scrollLimitX) scrollX = scrollLimitX;
        if (scrollX < 0) scrollX = 0;
        if (scrollY > scrollLimitY) scrollY = scrollLimitY;
        if (scrollY < 0) scrollY = 0;
    }
}
