package com.alrex.parcool.fabric.compat.event;

/** Base class of the adapter events; holds the cancel flag used by {@link ICancellableEvent}. */
public abstract class Event {
    boolean canceled;
}
