package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.zipline.ILoadedZiplineHolderProvider;
import com.alrex.parcool.common.zipline.LoadedZiplineHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements ILoadedZiplineHolderProvider {
    private LoadedZiplineHolder parcool$ziplineHolder = new LoadedZiplineHolder();


    @Override
    public LoadedZiplineHolder getZiplineHolder() {
        return parcool$ziplineHolder;
    }


    @Inject(method = "setBlocksDirty", at = @At("TAIL"))
    public void onSetBlocksDirty(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var parkourability = Parkourability.get(player);
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.BlockChangedInClientListener.class)) {
            listener.onChangeBlock(pos);
        }
    }
}
