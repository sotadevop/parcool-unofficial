package com.alrex.parcool.server.command.args;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.fabric.compat.IEventBus;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

public class ArgumentTypes {
    public static void register(IEventBus bus) {
        ArgumentTypeRegistry.registerArgumentType(
                ParCool.resourceLocation("action"),
                ActionArgumentType.class,
                SingletonArgumentInfo.contextFree(ActionArgumentType::new)
        );
    }
}
