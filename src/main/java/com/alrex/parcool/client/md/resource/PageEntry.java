package com.alrex.parcool.client.md.resource;

import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;

@Environment(EnvType.CLIENT)
public record PageEntry(String translationKey, Identifier resourceLocation) implements Comparable<PageEntry> {
    @Override
    public int compareTo(@Nonnull PageEntry o) {
        return resourceLocation.compareTo(o.resourceLocation);
    }
}
