package com.alrex.parcool.fabric.compat.network;

import net.minecraft.world.entity.player.Player;

/** What upstream's packet handlers read from NeoForge's payload context. */
public interface IPayloadContext {
    /** On the server the sending player, on the client the local player. */
    Player player();

    Flow flow();

    enum Flow {
        SERVERBOUND(LogicalSide.SERVER), CLIENTBOUND(LogicalSide.CLIENT);

        private final LogicalSide receptionSide;

        Flow(LogicalSide receptionSide) {
            this.receptionSide = receptionSide;
        }

        public LogicalSide getReceptionSide() {
            return receptionSide;
        }
    }
}
