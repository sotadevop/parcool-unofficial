package com.alrex.parcool.fabric.compat.event;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Player;

/** Fired in {@code LocalPlayer#aiStep} right after the movement input was read for this tick. */
public class MovementInputUpdateEvent extends PlayerEvent {
    private final ClientInput input;

    public MovementInputUpdateEvent(Player player, ClientInput input) {
        super(player);
        this.input = input;
    }

    public ClientInput getInput() {
        return input;
    }
}
