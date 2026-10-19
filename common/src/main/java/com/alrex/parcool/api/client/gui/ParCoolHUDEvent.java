package com.alrex.parcool.api.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.alrex.parcool.api.event.CancellableEvent;

public class ParCoolHUDEvent {

    private boolean canceled = false;

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }


    public static class RenderEvent extends ParCoolHUDEvent implements CancellableEvent {
        private final GuiGraphicsExtractor graphics;
        private final DeltaTracker partialTick;

        public RenderEvent(GuiGraphicsExtractor s, DeltaTracker partialTick) {
            this.graphics = s;
            this.partialTick = partialTick;
        }

        public GuiGraphicsExtractor getGuiGraphics() {
            return graphics;
        }

        public DeltaTracker getDeltaTracker() {
            return partialTick;
        }

    }
}
