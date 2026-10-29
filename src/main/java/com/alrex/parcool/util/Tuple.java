package com.alrex.parcool.util;

/**
 * Replacement for {@code net.minecraft.util.Tuple}, which Minecraft 26.3 removed.
 * Same shape as the vanilla class, so the call sites only needed their import changed.
 */
public final class Tuple<A, B> {
    private A a;
    private B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}
