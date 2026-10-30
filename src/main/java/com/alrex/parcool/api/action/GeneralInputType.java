package com.alrex.parcool.api.action;

import com.alrex.parcool.client.input.ParCoolKeyBinds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum GeneralInputType {
    NONE, INSTANT, CONTINUOUS;

    public enum Instant {
        PRESS_KEY, RELEASE_KEY;

        // Client only: creates the client-only input handles below (only called for the local player).
        @Environment(EnvType.CLIENT)
        public Input newInput() {
            return switch (this) {
                case PRESS_KEY -> new PressKeyInput();
                case RELEASE_KEY -> new ReleaseKeyInput();
            };
        }
    }

    public enum Continuation {
        DOWN_KEY, TOGGLE, ALWAYS;

        // Client only: creates the client-only input handles below (only called for the local player).
        @Environment(EnvType.CLIENT)
        public Input newInput() {
            return switch (this) {
                case DOWN_KEY -> new DownKeyInput();
                case TOGGLE -> new ToggleKeyInput();
                default -> new AlwaysInput();
            };
        }
    }

    public interface Input {
        boolean isActive();

        @Environment(EnvType.CLIENT)
        default void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
        }
    }

    @Environment(EnvType.CLIENT)
    private static abstract class AbstractInputHandle implements Input {
        protected boolean current;

        @Override
        public boolean isActive() {
            return current;
        }
    }

    @Environment(EnvType.CLIENT)
    private static class PressKeyInput extends AbstractInputHandle {
        @Override
        public void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
            current = stateProvider.state().isJustPressed();
        }
    }

    @Environment(EnvType.CLIENT)
    private static class ReleaseKeyInput extends AbstractInputHandle {
        @Override
        public void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
            current = stateProvider.state().isJustReleased();
        }
    }

    @Environment(EnvType.CLIENT)
    private static class DownKeyInput extends AbstractInputHandle {
        @Override
        public void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
            current = stateProvider.state().isDown();
        }
    }

    @Environment(EnvType.CLIENT)
    private static class ToggleKeyInput extends AbstractInputHandle {
        @Override
        public void tick(ParCoolKeyBinds.IStateProvider stateProvider) {
            if (stateProvider.state().isJustPressed()) current = !current;
        }
    }

    @Environment(EnvType.CLIENT)
    private static class AlwaysInput implements Input {
        @Override
        public boolean isActive() {
            return true;
        }
    }
}
