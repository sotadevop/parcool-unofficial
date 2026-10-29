package com.alrex.parcool.fabric.compat.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

/** The three NeoForge send helpers ParCool uses, on Fabric networking. */
public final class PacketDistributor {
    @Nullable
    private static volatile MinecraftServer server;

    private PacketDistributor() {
    }

    /** Called once at mod init: tracks the running server for {@link #sendToAllPlayers}. */
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
        ServerLifecycleEvents.SERVER_STOPPED.register(s -> server = null);
    }

    /** Client only; {@link ClientPlayNetworking} is resolved lazily, so servers never load it. */
    public static void sendToServer(CustomPacketPayload payload, CustomPacketPayload... payloads) {
        ClientPlayNetworking.send(payload);
        for (var p : payloads) ClientPlayNetworking.send(p);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        ServerPlayNetworking.send(player, payload);
        for (var p : payloads) ServerPlayNetworking.send(player, p);
    }

    public static void sendToAllPlayers(CustomPacketPayload payload, CustomPacketPayload... payloads) {
        var s = server;
        if (s == null) return;
        for (var player : PlayerLookup.all(s)) sendToPlayer(player, payload, payloads);
    }
}
