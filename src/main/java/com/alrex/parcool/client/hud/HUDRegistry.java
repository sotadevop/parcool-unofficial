package com.alrex.parcool.client.hud;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.hud.stamina.StaminaHUDController;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.ClientTickEvent;
import com.alrex.parcool.fabric.compat.event.RegisterGuiLayersEvent;

@Environment(EnvType.CLIENT)
public class HUDRegistry {
	private static HUDRegistry instance = null;

	private final StaminaHUDController staminaHUD = new StaminaHUDController();


	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

    public void onSetup(RegisterGuiLayersEvent event) {
        event.registerAbove(Identifier.fromNamespaceAndPath("minecraft", "food_level"), ParCool.resourceLocation("hud.stamina.host"), staminaHUD);
	}

    public void onTick(ClientTickEvent.Post event) {
		staminaHUD.onTick(event);
	}
}
