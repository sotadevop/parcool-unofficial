package com.alrex.parcool.fabric.compat.network;

import com.alrex.parcool.fabric.compat.event.Event;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers ParCool's payload types with Fabric's {@link PayloadTypeRegistry} and their server
 * receivers with {@link ServerPlayNetworking}. Client receivers are collected in
 * {@link #CLIENT_HANDLERS} and registered by {@code ParCoolClient}, because Fabric's client
 * networking class must only be loaded on the client.
 *
 * <p>Both Fabric receivers run on the main thread (server thread or client thread), like NeoForge's
 * default play payload handlers.
 */
public class RegisterPayloadHandlersEvent extends Event {
    public static final RegisterPayloadHandlersEvent INSTANCE = new RegisterPayloadHandlersEvent();
    public static final List<ClientHandler<?>> CLIENT_HANDLERS = new ArrayList<>();

    public record ClientHandler<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, IPayloadHandler<T> handler) {
    }

    /**
     * NeoForge negotiates {@code version} between client and server and refuses mismatches; Fabric
     * has no equivalent, so it is ignored. Both sides need the same ParCool version.
     */
    public Registrar registrar(String version) {
        return new Registrar();
    }

    public static class Registrar {
        public <T extends CustomPacketPayload, B extends io.netty.buffer.ByteBuf> void playBidirectional(
                CustomPacketPayload.Type<T> type, StreamCodec<? super B, T> codec, IPayloadHandler<T> handler) {
            register(type, castCodec(codec), handler, true);
        }

        public <T extends CustomPacketPayload, B extends io.netty.buffer.ByteBuf> void playToClient(
                CustomPacketPayload.Type<T> type, StreamCodec<? super B, T> codec, IPayloadHandler<T> handler) {
            register(type, castCodec(codec), handler, false);
        }

        @SuppressWarnings("unchecked")
        private static <T> StreamCodec<io.netty.buffer.ByteBuf, T> castCodec(StreamCodec<?, T> codec) {
            // Every ParCool codec is a StreamCodec<ByteBuf, T> (IHandler#codec), which is also valid for
            // the RegistryFriendlyByteBuf Fabric passes in.
            return (StreamCodec<io.netty.buffer.ByteBuf, T>) codec;
        }

        private static <T extends CustomPacketPayload> void register(
                CustomPacketPayload.Type<T> type, StreamCodec<io.netty.buffer.ByteBuf, T> codec,
                IPayloadHandler<T> handler, boolean toServer) {
            PayloadTypeRegistry.clientboundPlay().register(type, codec);
            CLIENT_HANDLERS.add(new ClientHandler<>(type, handler));
            if (!toServer) return;
            PayloadTypeRegistry.serverboundPlay().register(type, codec);
            ServerPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> handler.handle(payload, new IPayloadContext() {
                @Override
                public Player player() {
                    return ctx.player();
                }

                @Override
                public Flow flow() {
                    return Flow.SERVERBOUND;
                }
            }));
        }
    }
}
