package com.alrex.parcool.api.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.alrex.parcool.fabric.compat.event.Event;
import com.alrex.parcool.fabric.compat.event.ICancellableEvent;

public class RenderParCoolHUDEvent extends Event {
    public static class Render extends RenderParCoolHUDEvent {
        private final GuiGraphicsExtractor graphics;
        private final float partialTick;

        public Render(GuiGraphicsExtractor graphics, float partialTick) {
            this.graphics = graphics;
            this.partialTick = partialTick;
        }

        public GuiGraphicsExtractor getGraphics() {
            return graphics;
        }

        public float getPartialTick() {
            return partialTick;
        }

        public static class Stamina extends Render {
            private final StaminaDisplayContext currentContext;
            private final StaminaDisplayContext oldContext;

            public Stamina(GuiGraphicsExtractor graphics, float partialTick, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                super(graphics, partialTick);
                this.currentContext = currentContext;
                this.oldContext = oldContext;
            }

            public StaminaDisplayContext getCurrentContext() {
                return currentContext;
            }

            public StaminaDisplayContext getOldContext() {
                return oldContext;
            }

            public static class Pre extends Stamina implements ICancellableEvent {
                public Pre(GuiGraphicsExtractor graphics, float partialTick, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                    super(graphics, partialTick, currentContext, oldContext);
                }
            }

            public static class Post extends Stamina {
                public Post(GuiGraphicsExtractor graphics, float partialTick, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                    super(graphics, partialTick, currentContext, oldContext);
                }
            }
        }
    }

    public static class Update extends RenderParCoolHUDEvent {
        public static class StaminaContext extends Update {
            private final StaminaDisplayContext currentContext;
            private final StaminaDisplayContext oldContext;

            public StaminaContext(StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
                this.currentContext = currentContext;
                this.oldContext = oldContext;
            }

            public StaminaDisplayContext getCurrentContext() {
                return currentContext;
            }

            public StaminaDisplayContext getOldContext() {
                return oldContext;
            }
        }
    }
}
