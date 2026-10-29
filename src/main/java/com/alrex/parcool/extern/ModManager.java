package com.alrex.parcool.extern;

import net.fabricmc.loader.api.FabricLoader;

public abstract class ModManager {
    private boolean installed = false;
    private final String modId;

    public ModManager(String modId) {
        this.modId = modId;
    }

    public void init() {
        installed = FabricLoader.getInstance().isModLoaded(modId);
    }

    // These are called After `init` method
    public void initInClient() {
    }

    public void initInDedicatedServer() {
    }

    public boolean isInstalled() {
        return installed;
    }

    public String getModID() {
        return modId;
    }
}
