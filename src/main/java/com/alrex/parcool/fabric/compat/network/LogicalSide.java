package com.alrex.parcool.fabric.compat.network;

/** The logical side a packet is handled on (the integrated server counts as SERVER). */
public enum LogicalSide {
    CLIENT, SERVER;

    public boolean isClient() {
        return this == CLIENT;
    }

    public boolean isServer() {
        return this == SERVER;
    }
}
