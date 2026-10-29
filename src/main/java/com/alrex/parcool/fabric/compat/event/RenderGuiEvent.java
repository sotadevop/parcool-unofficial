package com.alrex.parcool.fabric.compat.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class RenderGuiEvent extends Event {
    private final GuiGraphicsExtractor guiGraphics;
    private final DeltaTracker partialTick;

    protected RenderGuiEvent(GuiGraphicsExtractor guiGraphics, DeltaTracker partialTick) {
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
    }

    public GuiGraphicsExtractor getGuiGraphics() {
        return guiGraphics;
    }

    public DeltaTracker getPartialTick() {
        return partialTick;
    }

    /** Fired after the vanilla HUD was drawn (registered last in HudElementRegistry). */
    public static class Post extends RenderGuiEvent {
        public Post(GuiGraphicsExtractor guiGraphics, DeltaTracker partialTick) {
            super(guiGraphics, partialTick);
        }
    }
}
