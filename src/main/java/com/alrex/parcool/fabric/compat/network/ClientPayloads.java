package com.alrex.parcool.fabric.compat.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

/** Registers the client receivers collected by {@link RegisterPayloadHandlersEvent}. */
@Environment(EnvType.CLIENT)
public final class ClientPayloads {
    private ClientPayloads() {
    }

    public static void registerReceivers() {
        for (var handler : RegisterPayloadHandlersEvent.CLIENT_HANDLERS) register(handler);
    }

    private static <T extends CustomPacketPayload> void register(RegisterPayloadHandlersEvent.ClientHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(handler.type(), (payload, ctx) -> handler.handler().handle(payload, new IPayloadContext() {
            @Override
            public Player player() {
                return ctx.player();
            }

            @Override
            public Flow flow() {
                return Flow.CLIENTBOUND;
            }
        }));
    }
}
