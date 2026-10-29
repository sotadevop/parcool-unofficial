package com.alrex.parcool.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.fabric.compat.network.LogicalSide;
import com.alrex.parcool.fabric.compat.network.IPayloadContext;

import javax.annotation.Nullable;
import java.util.UUID;

public class NetworkUtil {
    @Nullable
    public static Player getPlayerInLogicalClient(UUID playerID, IPayloadContext context, boolean castByClient) {
        boolean isInLogicalServer = context.flow().getReceptionSide() == LogicalSide.SERVER;
        if (isInLogicalServer) {
            var player = context.player();
            if (!player.getUUID().equals(playerID)) {
                return player.level().getPlayerByUUID(playerID);
            }
            return player;
        } else {
            var world = Minecraft.getInstance().level;
            if (world == null) return null;
            var player = world.getPlayerByUUID(playerID);
            if (castByClient && player != null && player.isLocalPlayer()) return null;
            return player;
        }
    }

    @Nullable
    public static Player getPlayerInLogicalServer(UUID playerID, IPayloadContext context) {
        var player = context.player();
        if (player.getUUID().equals(playerID)) {
            return player;
        }
        // Fabric port: upstream returned the player with that UUID, so a modified client could change
        // another player's ParCool state. A client only ever reports its own state, so anything else
        // is rejected.
        return null;
    }
}
