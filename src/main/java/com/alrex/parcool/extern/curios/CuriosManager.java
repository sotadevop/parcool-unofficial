package com.alrex.parcool.extern.curios;

import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

/**
 * Fabric port: Curios only exists for NeoForge, so this stub always reports "not installed" and
 * keeps the upstream call sites unchanged. The upstream integration lives in
 * {@code extern/curios} of ParCool 4.0 (NeoForge).
 */
public class CuriosManager extends ModManager {
    public CuriosManager() {
        super("curios");
    }

    @Override
    public boolean isInstalled() {
        return false;
    }

    public Stream<ItemStack> getGeneralEquipments(Player player) {
        return Stream.empty();
    }

    public Stream<ItemStack> getRenderAbleGeneralEquipments(Player player) {
        return Stream.empty();
    }

    public boolean isEquipped(Player player, Item item) {
        return false;
    }
}
