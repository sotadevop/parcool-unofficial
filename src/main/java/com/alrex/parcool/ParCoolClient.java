package com.alrex.parcool;

import com.alrex.parcool.registrar.ClientRegistrar;
import net.fabricmc.api.ClientModInitializer;

public class ParCoolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRegistrar.init();
    }
}
