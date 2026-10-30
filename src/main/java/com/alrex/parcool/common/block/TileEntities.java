package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.alrex.parcool.fabric.compat.event.IEventBus;
import com.alrex.parcool.fabric.compat.registry.DeferredRegister;
import com.alrex.parcool.fabric.compat.registry.DeferredHolder;

public class TileEntities {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ParCool.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ZiplineHookTileEntity>> ZIPLINE_HOOK = REGISTER.register(
            "zipline_hook",
            () -> new BlockEntityType<>(
                    (pos, state) -> new ZiplineHookTileEntity(TileEntities.ZIPLINE_HOOK.get(), pos, state),
                    java.util.Set.of(Blocks.WOODEN_ZIPLINE_HOOK.get(), Blocks.IRON_ZIPLINE_HOOK.get())
            )
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
