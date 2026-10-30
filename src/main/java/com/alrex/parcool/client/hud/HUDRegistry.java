package com.alrex.parcool.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.hud.stamina.StaminaHUDController;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.ClientTickEvent;
import com.alrex.parcool.fabric.compat.event.RegisterGuiLayersEvent;

@Environment(EnvType.CLIENT)
public class HUDRegistry {
	public static final Identifier STAMINA_ID = ParCool.resourceLocation("hud.stamina.host");
	private static HUDRegistry instance = null;

	private final StaminaHUDController staminaHUD = new StaminaHUDController();


	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

    public void onSetup(RegisterGuiLayersEvent event) {
        event.registerAbove(Identifier.fromNamespaceAndPath("minecraft", "food_level"), STAMINA_ID, staminaHUD);
        // NeoForge stacked bars through Gui#rightHeight; Fabric asks each element for its height, so
        // the bars above the stamina row (air bubbles, mount health) move up instead of overlapping.
        HudStatusBarHeightRegistry.addRight(STAMINA_ID, player -> staminaHUD.statusBarHeight());
	}

    public void onTick(ClientTickEvent.Post event) {
		staminaHUD.onTick(event);
	}
}
