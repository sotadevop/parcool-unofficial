package com.alrex.parcool.api.stamina;

import net.minecraft.resources.Identifier;

public record StaminaTypeEntry<T extends AbstractLocalStamina>(Identifier id, String name, IStaminaProvider<T> provider) {
}
