package com.alrex.parcool.client.input;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class InputUtil {
    public static Vec3 getInputVectorInWorld(Player player, ClientInput input) {
        return new Vec3(input.getMoveVector().x, 0, input.getMoveVector().y).yRot((float) Math.toRadians(-player.getYRot()));
    }
}
