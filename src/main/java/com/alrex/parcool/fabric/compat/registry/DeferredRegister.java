package com.alrex.parcool.fabric.compat.registry;

import com.alrex.parcool.fabric.compat.event.IEventBus;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * NeoForge's {@code DeferredRegister} API shape on top of Fabric's immediate registration: each
 * {@code register} call registers right away (Fabric allows that during mod initialization) and
 * returns a {@link DeferredHolder}. {@link #register(IEventBus)} is therefore a no-op.
 *
 * <p>Because entries are created in declaration order, an entry may only reference entries of
 * registries that were initialized before (ParCool registers blocks before items, effects before
 * potions).
 */
public class DeferredRegister<R> {
    private final Registry<R> registry;
    private final String namespace;

    protected DeferredRegister(Registry<R> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    public static <R> DeferredRegister<R> create(Registry<R> registry, String namespace) {
        return new DeferredRegister<>(registry, namespace);
    }

    @SuppressWarnings("unchecked")
    public static <R> DeferredRegister<R> create(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
        var registry = BuiltInRegistries.REGISTRY.getValue((ResourceKey) registryKey);
        if (registry == null) throw new IllegalArgumentException("Unknown registry " + registryKey);
        return new DeferredRegister<>((Registry<R>) registry, namespace);
    }

    public static DataComponents createDataComponents(ResourceKey<Registry<DataComponentType<?>>> registryKey, String namespace) {
        return new DataComponents(namespace);
    }

    public ResourceKey<R> key(String name) {
        return ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(namespace, name));
    }

    public <T extends R> DeferredHolder<R, T> register(String name, Supplier<? extends T> supplier) {
        return new DeferredHolder<>(Registry.registerForHolder(registry, key(name), supplier.get()));
    }

    public <T extends R> DeferredHolder<R, T> register(String name, Function<Identifier, ? extends T> factory) {
        var key = key(name);
        return new DeferredHolder<>(Registry.registerForHolder(registry, key, factory.apply(key.identifier())));
    }

    /** NeoForge attaches the register to the mod bus here; on Fabric everything is registered already. */
    public void register(IEventBus bus) {
    }

    public static class DataComponents extends DeferredRegister<DataComponentType<?>> {
        protected DataComponents(String namespace) {
            super(BuiltInRegistries.DATA_COMPONENT_TYPE, namespace);
        }

        public <D> DeferredHolder<DataComponentType<?>, DataComponentType<D>> registerComponentType(
                String name, UnaryOperator<DataComponentType.Builder<D>> builder) {
            return register(name, () -> builder.apply(DataComponentType.builder()).build());
        }
    }
}
