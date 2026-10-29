package com.alrex.parcool.fabric.compat.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Picks the client or the server handler by the side the payload arrived on. */
public record DirectionalPayloadHandler<T extends CustomPacketPayload>(
        IPayloadHandler<T> clientSide, IPayloadHandler<T> serverSide) implements IPayloadHandler<T> {
    @Override
    public void handle(T payload, IPayloadContext context) {
        (context.flow().getReceptionSide().isServer() ? serverSide : clientSide).handle(payload, context);
    }
}
