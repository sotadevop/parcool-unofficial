package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.client.gui.RenderParCoolHUDEvent;
import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.AbstractClientPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.ClientTickEvent;
import com.alrex.parcool.fabric.compat.event.ParCoolEventBus;

import javax.annotation.Nonnull;

@Environment(EnvType.CLIENT)
public class StaminaHUDController implements LayeredDraw.Layer {
	LightStaminaHUD lightStaminaHUD;

	private StaminaDisplayContext currentContext = StaminaDisplayContext.DEFAULT;
	private StaminaDisplayContext oldContext = StaminaDisplayContext.DEFAULT;
	private int tickValueNotChange;

	public StaminaHUDController() {
		lightStaminaHUD = new LightStaminaHUD();
	}

    public void onTick(ClientTickEvent event) {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		oldContext = currentContext;
		if (Parkourability.get(player).getStamina() instanceof AbstractLocalStamina localStamina) {
			currentContext = oldContext.next(localStamina);
		}
		lightStaminaHUD.tick(player, currentContext, oldContext);
		if (currentContext.equals(oldContext)) {
			tickValueNotChange++;
		} else {
			tickValueNotChange = 0;
		}

        ParCoolEventBus.post(new RenderParCoolHUDEvent.Update.StaminaContext(currentContext, oldContext));
	}

	@Override
    public void extractRenderState(@Nonnull GuiGraphicsExtractor guiGraphics, @Nonnull DeltaTracker deltaTracker) {
		AbstractClientPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);

		if (tickValueNotChange > 40 && !ParCool.getConfig().client().staminaHud.showAlways().get()) {
			return;
		}
		if (parkourability.getStamina() instanceof AbstractLocalStamina localStamina && !localStamina.showHud()) {
			return;
		}

        if (ParCoolEventBus.post(new RenderParCoolHUDEvent.Render.Stamina.Pre(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true), currentContext, oldContext)).isCanceled())
			return;

		if (ParCool.getConfig().client().staminaHud.type().get() == HUDType.Light) {
            lightStaminaHUD.extractRenderState(guiGraphics, parkourability, currentContext, oldContext, deltaTracker);
		}

        ParCoolEventBus.post(new RenderParCoolHUDEvent.Render.Stamina.Post(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true), currentContext, oldContext));
	}
}
