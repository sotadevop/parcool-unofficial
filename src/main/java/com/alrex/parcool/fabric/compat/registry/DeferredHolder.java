package com.alrex.parcool.fabric.compat.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A registered entry, usable both as a {@link Holder} (upstream passes effects, attributes and
 * potions as holders) and as a {@link Supplier} of the concrete type.
 *
 * <p>Unlike NeoForge's, the entry is already registered when this object exists (Fabric registers
 * immediately), so it simply delegates to the registry's {@link Holder.Reference}.
 */
public final class DeferredHolder<R, T extends R> implements Holder<R>, Supplier<T> {
    private final Holder.Reference<R> delegate;

    DeferredHolder(Holder.Reference<R> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        return (T) delegate.value();
    }

    public Identifier getId() {
        return delegate.key().identifier();
    }

    public ResourceKey<R> getKey() {
        return delegate.key();
    }

    public Holder<R> getDelegate() {
        return delegate;
    }

    @Override
    public R value() {
        return delegate.value();
    }

    @Override
    public boolean isBound() {
        return delegate.isBound();
    }

    @Override
    public boolean areComponentsBound() {
        return delegate.areComponentsBound();
    }

    @Override
    public boolean is(Identifier id) {
        return delegate.is(id);
    }

    @Override
    public boolean is(ResourceKey<R> key) {
        return delegate.is(key);
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return delegate.is(predicate);
    }

    @Override
    public boolean is(TagKey<R> tag) {
        return delegate.is(tag);
    }

    @Override
    public boolean is(Holder<R> holder) {
        return delegate.is(holder);
    }

    @Override
    public Stream<TagKey<R>> tags() {
        return delegate.tags();
    }

    @Override
    public DataComponentMap components() {
        return delegate.components();
    }

    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return delegate.unwrap();
    }

    @Override
    public Optional<ResourceKey<R>> unwrapKey() {
        return delegate.unwrapKey();
    }

    @Override
    public Kind kind() {
        return delegate.kind();
    }

    @Override
    public boolean canSerializeIn(HolderOwner<R> owner) {
        return delegate.canSerializeIn(owner);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DeferredHolder<?, ?> other ? delegate.equals(other.delegate) : delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return "DeferredHolder{" + delegate.key() + "}";
    }
}
