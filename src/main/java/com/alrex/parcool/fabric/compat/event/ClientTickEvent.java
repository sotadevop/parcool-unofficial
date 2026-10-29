package com.alrex.parcool.fabric.compat.event;

public abstract class ClientTickEvent extends Event {
    /** Fired at the start of {@code Minecraft#tick}. */
    public static class Pre extends ClientTickEvent {
        public static final Pre INSTANCE = new Pre();
    }

    /** Fired at the end of {@code Minecraft#tick}. */
    public static class Post extends ClientTickEvent {
        public static final Post INSTANCE = new Post();
    }
}
