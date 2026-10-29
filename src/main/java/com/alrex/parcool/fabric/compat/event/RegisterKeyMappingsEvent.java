package com.alrex.parcool.fabric.compat.event;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class RegisterKeyMappingsEvent extends Event {
    public static final RegisterKeyMappingsEvent INSTANCE = new RegisterKeyMappingsEvent();

    public void register(KeyMapping key) {
        KeyMappingHelper.registerKeyMapping(key);
    }
}
