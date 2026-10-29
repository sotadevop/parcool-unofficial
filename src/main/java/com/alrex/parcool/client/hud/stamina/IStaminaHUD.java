package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IStaminaHUD {
    void extractRenderState(
            GuiGraphicsExtractor graphics,
            Parkourability parkourability,
            StaminaDisplayContext currentContext, StaminaDisplayContext oldContext,
            DeltaTracker deltaTracker
    );

    default void tick(Player player, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
    }
}
