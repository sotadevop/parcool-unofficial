package com.alrex.parcool.platform;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

/**
 * Fabric network plumbing on Architectury's typed {@link NetworkManager} API.
 *
 * <h2>26.3</h2>
 * Architectury 22 removed the id-based raw-buffer overloads this class used on 1.21.11; registering
 * and sending now go through the payload's {@link CustomPacketPayload.Type}. The {@code wireId}
 * parameters are therefore unused here: the wire id is the type's own id. Payloads that travel in
 * both directions share one type, which is fine because Fabric keeps separate payload registries for
 * the clientbound and the serverbound direction.
 */
public class FabricParCoolNetwork implements ParCoolNetwork {

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends CustomPacketPayload> StreamCodec<? super RegistryFriendlyByteBuf, T> erase(StreamCodec<?, T> codec) {
        // The payload codecs are declared over FriendlyByteBuf; RegistryFriendlyByteBuf is a subtype.
        return (StreamCodec) codec;
    }

    @Override
    public <T extends CustomPacketPayload> void register(
            Class<T> payloadClass,
            CustomPacketPayload.Type<T> type,
            Identifier wireId,
            StreamCodec<?, T> codec,
            boolean clientbound,
            BiConsumer<T, NetworkManager.PacketContext> handler) {
        NetworkManager.NetworkReceiver<T> receiver =
                (payload, context) -> context.queue(() -> handler.accept(payload, context));
        if (clientbound) {
            // Safe from common init on any side: registers the type for sending and, on a client,
            // the receiver.
            NetworkManager.registerS2C(type, erase(codec), receiver);
        } else {
            NetworkManager.registerC2S(type, erase(codec), receiver);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void sendToServer(
            T payload, Identifier wireId, StreamCodec<?, T> codec) {
        NetworkManager.sendToServer(payload);
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayer(
            ServerPlayer player, T payload, Identifier wireId, StreamCodec<?, T> codec) {
        NetworkManager.sendToPlayer(player, payload);
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayers(
            Iterable<ServerPlayer> players, T payload, Identifier wireId, StreamCodec<?, T> codec) {
        NetworkManager.sendToPlayers(players, payload);
    }
}
