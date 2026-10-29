package com.alrex.parcool.fabric.compat.event;

import net.minecraft.world.level.Level;

public abstract class LevelTickEvent extends Event {
    private final Level level;

    protected LevelTickEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    /** Fired at the end of a level tick, on both logical sides. */
    public static class Post extends LevelTickEvent {
        public Post(Level level) {
            super(level);
        }
    }
}
