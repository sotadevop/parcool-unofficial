package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import com.alrex.parcool.fabric.compat.event.IEventBus;
import com.alrex.parcool.fabric.compat.registry.DeferredRegister;
import com.alrex.parcool.fabric.compat.registry.DeferredHolder;

public class ParCoolPotions {
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ParCool.MOD_ID);
    public static final DeferredHolder<Potion, Potion> POOR_ENERGY_DRINK =
			POTIONS.register(
					"poor_energy_drink",
					() -> new Potion("poor_energy_drink", 
                            new MobEffectInstance(ParCoolMobEffects.INEXHAUSTIBLE.getDelegate(), 3600/*3 min*/),
							new MobEffectInstance(MobEffects.HUNGER, 100),
							new MobEffectInstance(MobEffects.POISON, 100)
					)
			);
    public static final DeferredHolder<Potion, Potion> ENERGY_DRINK =
			POTIONS.register(
					"energy_drink",
					() -> new Potion("energy_drink", 
                            new MobEffectInstance(ParCoolMobEffects.INEXHAUSTIBLE.getDelegate(), 8400/*7 min*/)
					)
			);

	public static void register(IEventBus modBus) {
		POTIONS.register(modBus);
	}
}
