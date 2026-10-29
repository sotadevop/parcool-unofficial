package com.alrex.parcool.fabric.compat.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/**
 * A registered entry, as returned by {@link DeferredRegister#register}.
 *
 * <p>NeoForge's DeferredHolder also implements {@link Holder}. That is not possible on 26.x, where
 * {@code Holder} is a sealed interface, so code that needs a holder calls {@link #getDelegate()}
 * (porting/mc_26_3.py adds that for effects, attributes and potions). The delegate is the
 * registry's own {@link Holder.Reference}, so holder comparisons keep working.
 *
 * <p>Unlike NeoForge's, the entry is already registered when this object exists.
 */
public final class DeferredHolder<R, T extends R> implements Supplier<T> {
    private final Holder.Reference<R> delegate;

    DeferredHolder(Holder.Reference<R> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        return (T) delegate.value();
    }

    public R value() {
        return delegate.value();
    }

    public Holder<R> getDelegate() {
        return delegate;
    }

    public Identifier getId() {
        return delegate.key().identifier();
    }

    public ResourceKey<R> getKey() {
        return delegate.key();
    }

    @Override
    public String toString() {
        return "DeferredHolder{" + delegate.key() + "}";
    }
}
