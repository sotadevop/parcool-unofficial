package com.alrex.parcool.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.alrex.parcool.fabric.compat.network.DirectionalPayloadHandler;
import com.alrex.parcool.fabric.compat.network.IPayloadContext;

public interface IHandler<MSG extends CustomPacketPayload> {
    default StreamCodec<ByteBuf, MSG> codec() {
        return StreamCodec.of(this::encode, this::decode);
    }

    default DirectionalPayloadHandler<MSG> payloadHandler() {
        return new DirectionalPayloadHandler<>(
                this::handleInLogicalClient, this::handleInLogicalServer
        );
    }

    void encode(ByteBuf packet, MSG msg);

    MSG decode(ByteBuf packet);

    void handleInLogicalServer(MSG msg, IPayloadContext context);

    void handleInLogicalClient(MSG msg, IPayloadContext context);
}
