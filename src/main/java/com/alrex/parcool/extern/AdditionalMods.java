package com.alrex.parcool.extern;

import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.extern.curios.CuriosManager;
import com.alrex.parcool.extern.epicfight.EpicFightManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Fabric port: upstream 4.0 integrates Curios, Better Third Person, Shoulder Surfing and Epic Fight
 * (NeoForge builds). None of them is ported yet; the Curios and Epic Fight entries are stubs that
 * report "not installed", and the camera mods' key remapping returns no override.
 */
public enum AdditionalMods {
    CURIOS(CuriosManager::new),
    EPIC_FIGHT(EpicFightManager::new);

    private final ModManager manager;

    AdditionalMods(Supplier<ModManager> supplier) {
        manager = supplier.get();
    }

    public ModManager get() {
        return manager;
    }

    public static CuriosManager curios() {
        return (CuriosManager) CURIOS.get();
    }

    public static EpicFightManager epicFight() {
        return (EpicFightManager) EPIC_FIGHT.get();
    }

    public static void init() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::init);
    }

    public static void initInClient() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::initInClient);
    }

    public static void initInDedicatedServer() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::initInDedicatedServer);
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static ParCoolKeyBinds.LogicalInput getLogicalKey(LogicalMovement movement) {
        return null;
    }
}
