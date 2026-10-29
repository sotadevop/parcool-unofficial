package com.alrex.parcool.fabric.compat.event;

import net.minecraft.client.DeltaTracker;

public abstract class RenderFrameEvent extends Event {
    private final DeltaTracker partialTick;

    protected RenderFrameEvent(DeltaTracker partialTick) {
        this.partialTick = partialTick;
    }

    public DeltaTracker getPartialTick() {
        return partialTick;
    }

    /** Fired at the start of each rendered frame. */
    public static class Pre extends RenderFrameEvent {
        public Pre(DeltaTracker partialTick) {
            super(partialTick);
        }
    }
}
