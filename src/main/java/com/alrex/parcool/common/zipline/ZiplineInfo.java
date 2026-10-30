package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.item.misc.ZiplineRopeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public record ZiplineInfo(ZiplineType type, int color) {

    public Tag save() {
        var tag = new CompoundTag();
        tag.putInt("color", color);
        tag.putByte("type", (byte) type().ordinal());
        return tag;
    }

    public void save(ValueOutput out) {
        out.putInt("color", color);
        out.putByte("type", (byte) type().ordinal());
    }

    public static ZiplineInfo load(ValueInput in) {
        int color = in.getIntOr("color", ZiplineRopeItem.DEFAULT_COLOR);
        int type = Byte.toUnsignedInt(in.getByteOr("type", (byte) ZiplineType.LOOSE.ordinal()));
        return new ZiplineInfo(ZiplineType.values()[type % ZiplineType.values().length], color);
    }

    public static ZiplineInfo load(@Nullable Tag tag) {
        if (tag instanceof CompoundTag cTag) {
            int color = cTag.contains("color") ? cTag.getIntOr("color", 0) : ZiplineRopeItem.DEFAULT_COLOR;
            ZiplineType type = cTag.contains("type") ?
                    ZiplineType.values()[cTag.getByteOr("type", (byte) 0) % ZiplineType.values().length] :
                    ZiplineType.LOOSE;
            return new ZiplineInfo(type, color);
        }
        return new ZiplineInfo(ZiplineType.LOOSE, ZiplineRopeItem.DEFAULT_COLOR);
    }
}
