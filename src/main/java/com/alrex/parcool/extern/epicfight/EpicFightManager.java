package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;

/**
 * Fabric port: Epic Fight compatibility is not ported (no 26.3 Fabric version to build against),
 * so this stub reports "not installed" and keeps the upstream call sites unchanged.
 */
public class EpicFightManager extends ModManager {
    public EpicFightManager() {
        super("epicfight");
    }

    @Override
    public boolean isInstalled() {
        return false;
    }

    public boolean isBattleMode(Player player) {
        return false;
    }
}
