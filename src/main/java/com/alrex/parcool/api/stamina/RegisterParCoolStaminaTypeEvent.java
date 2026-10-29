package com.alrex.parcool.api.stamina;

import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.fabric.compat.event.Event;

public class RegisterParCoolStaminaTypeEvent extends Event {
    private final StaminaTypeRegistry registry;

    public RegisterParCoolStaminaTypeEvent(StaminaTypeRegistry registry) {
        this.registry = registry;
    }

    public void register(StaminaTypeEntry<?> entry) {
        registry.register(entry);
    }
}
