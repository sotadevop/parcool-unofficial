package com.alrex.parcool;

import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.ParCoolMobEffects;
import com.alrex.parcool.api.ParCoolPotions;
import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.RegisterParCoolActionEvent;
import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.common.RegistryHash;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.item.ParCoolDataComponents;
import com.alrex.parcool.common.item.ParCoolItemGroup;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.fabric.compat.event.IEventBus;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.registrar.CommonRegistrar;
import com.alrex.parcool.server.command.args.ArgumentTypes;
import net.minecraft.resources.Identifier;
import com.alrex.parcool.fabric.compat.event.ParCoolEventBus;

public class ParCool implements ModInitializer {
	public static final String MOD_ID = "parcool";

	private static final ActionRegistry actionRegistry = new ActionRegistry();
	private static final StaminaTypeRegistry staminaTypeRegistry = new StaminaTypeRegistry();
	private static final ActionProcessor actionProcessor = new ActionProcessor();
	private static ParCoolConfig config;
	private static String connectionId;

	public static Identifier resourceLocation(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public static StaminaTypeRegistry getStaminaTypeRegistry() {
		return staminaTypeRegistry;
	}

	public static ParCoolConfig getConfig() {
		return config;
	}

	public static String getConnectionId() {
		return connectionId;
	}

	public static ActionProcessor getActionProcessor() {
		return actionProcessor;
	}

	/**
	 * Fabric port of upstream's {@code @Mod} constructor. Registration runs in the same order; the
	 * NeoForge bus subscriptions are replaced by {@link CommonRegistrar#init()}.
	 */
	@Override
	public void onInitialize() {
		var eventBus = IEventBus.INSTANCE;

		ParCoolMobEffects.register(eventBus);
		ParCoolPotions.register(eventBus);
		ParCoolAttributes.register(eventBus);
		ParCoolSoundEvents.register(eventBus);
		Blocks.register(eventBus);
		ParCoolItems.register(eventBus);
		Recipes.register(eventBus);
		EntityTypes.register(eventBus);
		TileEntities.register(eventBus);
		ParCoolItemGroup.register(eventBus);
		ParCoolDataComponents.register(eventBus);
		ArgumentTypes.register(eventBus);

		AdditionalMods.init();

		var staminaEvent = new RegisterParCoolStaminaTypeEvent(staminaTypeRegistry);
		com.alrex.parcool.common.stamina.StaminaTypes.onRegister(staminaEvent);
		eventBus.post(staminaEvent);
		staminaTypeRegistry.freeze();
		var actionEvent = new RegisterParCoolActionEvent(actionRegistry);
		com.alrex.parcool.common.action.ParCoolActions.onRegister(actionEvent);
		eventBus.post(actionEvent);
		actionRegistry.freeze();
		config = new ParCoolConfig(actionRegistry, staminaTypeRegistry);
		config.register(MOD_ID);
		var version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
		connectionId = version + "/" + RegistryHash.getHash(actionRegistry, staminaTypeRegistry);

		CommonRegistrar.init();
		if (FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.SERVER) {
			AdditionalMods.initInDedicatedServer();
		}
	}
}
