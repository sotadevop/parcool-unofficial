package com.alrex.parcool.client.gui.screen;

import com.mojang.blaze3d.Blaze3D;

import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.alrex.parcool.client.gui.components.CardPanel;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.alrex.parcool.util.Tuple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

@Environment(EnvType.CLIENT)
public class ParCoolTabletScreen extends Screen {
    public static final Identifier TEXTURE_LOCATION = ParCool.resourceLocation("textures/gui/parcool_screen.png");
    private static final int FRAME_WIDTH = 256;
    private static final int FRAME_HEIGHT = 160;
    private static final int CONTENT_WIDTH = 246;
    private static final int CONTENT_HEIGHT = 136;
    protected final GuiColorPallet colors;
    private Component topBarText = Component.empty();
    private AbstractButton topBarButton;
    private int frameOffsetX = 0;
    private int frameOffsetY = 0;
    protected int contentOffsetX = 0;
    protected int contentOffsetY = 0;
    protected int contentWidth = 0;
    protected int contentHeight = 0;
    private boolean fullscreen;
    protected final boolean openedByGuideItem;

    protected ParCoolTabletScreen(Component title, GuiColorPallet colors, boolean openedByGuideItem) {
        super(title);
        this.colors = colors;
        this.openedByGuideItem = openedByGuideItem;
        this.fullscreen = !openedByGuideItem;
    }


    @Override
    protected final void init() {
        super.init();
        frameOffsetX = (width - FRAME_WIDTH) / 2;
        frameOffsetY = (height - FRAME_HEIGHT) / 2;
        if (fullscreen) {
            contentOffsetX = 0;
            contentOffsetY = 0;
            contentWidth = width;
            contentHeight = height;
        } else {
            contentOffsetX = frameOffsetX + 5;
            contentOffsetY = frameOffsetY + 19;
            contentWidth = CONTENT_WIDTH;
            contentHeight = CONTENT_HEIGHT;
        }
        var sideBarButtons = getSideBarButtons();
        if (sideBarButtons == null) {
            initWidgets();
        } else {
            contentOffsetX += 13;
            contentWidth -= 13;
            initWidgets();
            addRenderableOnly(new CardPanel(contentOffsetX - 13, contentOffsetY, 13, contentHeight, colors.surface(), colors.shadow()));
            var buttonOffsetX = contentOffsetX - 12;
            var buttonOffsetY = contentOffsetY + 1;
            for (var i = 0; i < sideBarButtons.topButtonProviders.size(); i++) {
                var topButtonProvider = sideBarButtons.topButtonProviders.get(i);
                var button = addRenderableWidget(topButtonProvider.getA().create(buttonOffsetX, buttonOffsetY, topButtonProvider.getB()));
                buttonOffsetY += button.getHeight() + 1;
            }
            buttonOffsetY = contentOffsetY + contentHeight - 13;
            for (var i = 0; i < sideBarButtons.bottomButtonProviders.size(); i++) {
                var bottomButtonProvider = sideBarButtons.bottomButtonProviders.get(i);
                var button = addRenderableWidget(bottomButtonProvider.getA().create(buttonOffsetX, buttonOffsetY, bottomButtonProvider.getB()));
                buttonOffsetY -= button.getHeight() + 1;
            }
        }
        topBarButton = addWidget(new IconButton.Back(frameOffsetX + 7, frameOffsetY + 6, this::onPressTobBarButton));
        topBarButton.active = !fullscreen;
    }

    protected void initWidgets() {
    }

    public void setFullscreen(boolean fullscreen) {
        if (!openedByGuideItem) fullscreen = true;
        if (this.fullscreen != fullscreen) {
            this.fullscreen = fullscreen;
            rebuildWidgets();
        }
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    @Nullable
    protected SideBarButtons getSideBarButtons() {
        return null;
    }

    @Override
    public void extractRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        if (minecraft == null) return;
        var poseStack = graphics.pose();
        extractBackground(graphics, mouseX, mouseY, partial);
        poseStack.pushMatrix();
        {
            renderContent(graphics, mouseX, mouseY, partial);
            if (!fullscreen) {
                renderFrame(graphics, partial);
                topBarButton.extractRenderState(graphics, mouseX, mouseY, partial);
            }
        }
        poseStack.popMatrix();
    }

    private void renderFrame(GuiGraphicsExtractor graphics, float partial) {
        var poseStack = graphics.pose();
        poseStack.pushMatrix();
        {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_LOCATION, frameOffsetX, frameOffsetY, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, 256, 256);
            GuiRenderUtil.enableScissorTestInGuiCoordinate(graphics, frameOffsetX + 22, frameOffsetY + 7, 224, font.lineHeight + 5);
            graphics.text(font, topBarText, frameOffsetX + 23, frameOffsetY + 8, 0xFF37474F, false);
            graphics.disableScissor();
        }
        poseStack.popMatrix();
    }

    protected void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        super.extractRenderState(graphics, mouseX, mouseY, partial);
    }

    protected void setTopBarText(Component text) {
        this.topBarText = text;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        for (var iterator = this.children().listIterator(this.children().size()); iterator.hasPrevious(); ) {
            var listener = iterator.previous();
            if (listener.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), false)) {
                this.setFocused(listener);
                if (button == 0) this.setDragging(true);
                return true;
            }
        }
        return false;
    }

    public static class IconButton extends AbstractButton {
        private final TextureAtlasSprite sprite;
        @Nullable
        private final Runnable pressListener;

        public IconButton(int x, int y, TextureAtlasSprite sprite, @Nullable Runnable listener) {
            super(x, y, 11, 11, Component.empty());
            this.sprite = sprite;
            this.pressListener = listener;
        }

        @Override
        public void extractContents(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
            int tint = isHovered ? GuiRenderUtil.tint(0.8f, 0.8f, 0.8f, 1f) : ~0;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height, tint);
        }

        @Override
        public void onPress(InputWithModifiers input) {
            if (pressListener != null) pressListener.run();
        }

        @Override
        public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        }

        public interface Provider {
            IconButton create(int x, int y, @Nullable Runnable listener);
        }

        public static class Back extends IconButton {
            public Back(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_BACK), listener);
            }
        }

        public static class Home extends IconButton {
            public Home(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_HOME), listener);
            }
        }

        public static class Hamburger extends IconButton {
            public Hamburger(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_HAMBURGER), listener);
            }
        }

        public static class SlideToLeft extends IconButton {
            public SlideToLeft(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_CLOSE), listener);
            }
        }

        public static class Expand extends IconButton {
            public Expand(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_EXPAND), listener);
            }
        }

        public static class Shrink extends IconButton {
            public Shrink(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_SHRINK), listener);
            }
        }
    }

    protected record SideBarButtons(List<Tuple<IconButton.Provider, Runnable>> topButtonProviders,
                                    List<Tuple<IconButton.Provider, Runnable>> bottomButtonProviders) {
    }

    protected void confirmLink(boolean confirmed, String uri) {
        if (confirmed) {
            Blaze3D.openUri(java.net.URI.create(uri));
        }
        if (minecraft != null) minecraft.gui.setScreen(this);
    }

    protected void onPressTobBarButton() {
    }
}
