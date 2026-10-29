package com.alrex.parcool.server.command.args;

import com.alrex.parcool.ParCool;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import com.alrex.parcool.fabric.compat.event.IEventBus;
import com.alrex.parcool.fabric.compat.registry.DeferredHolder;
import com.alrex.parcool.fabric.compat.registry.DeferredRegister;

public class ArgumentTypes {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, ParCool.MOD_ID);
    public static DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<ActionArgumentType, ?>> ACTION = ARGUMENTS.register(
            "action", () -> ArgumentTypeInfos.registerByClass(ActionArgumentType.class, SingletonArgumentInfo.contextFree(ActionArgumentType::new))
    );

    public static void register(IEventBus bus) {
        ARGUMENTS.register(bus);
    }
}
