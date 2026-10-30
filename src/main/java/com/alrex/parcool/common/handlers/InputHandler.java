package com.alrex.parcool.common.handlers;

import net.minecraft.world.entity.player.Input;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.gui.GuiHelper;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import net.minecraft.client.Minecraft;
import com.alrex.parcool.fabric.compat.event.ClientTickEvent;
import com.alrex.parcool.fabric.compat.event.InputEvent;
import com.alrex.parcool.fabric.compat.event.MovementInputUpdateEvent;

public class InputHandler {
    public static void onTick(ClientTickEvent.Pre event) {
        if (ParCoolKeyBinds.OPEN_SETTING_SKILLTREE.state().isJustPressed()) {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            if (ParCool.getConfig().server().enableSkillTreeUi.get()) {
                GuiHelper.openSkillTreeGui(player);
            } else {
                GuiHelper.openSettingGui(player);
            }
        }
    }

    public static void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (event.isAttack() && com.alrex.parcool.common.item.misc.GrapplingHookItem.isHeld(player)) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
        var parkourability = Parkourability.get(player);
        parkourability.getAdditionalProperties().onJump();
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.KeyMapTriggeredListener.class)) {
            listener.onInput(event);
        }
    }

    public static void onPlayerInputUpdated(MovementInputUpdateEvent event) {
        if (!event.getEntity().isLocalPlayer()) return;

        var parkourability = Parkourability.get(event.getEntity());
        if (parkourability == null) return;

        if (parkourability.getBehaviorEnforcer().noSneakMarks.enforce()) {
            var keys = event.getInput().keyPresses;
            // Input is an immutable record in 26.x, so replace it with shift released.
            event.getInput().keyPresses = new Input(keys.forward(), keys.backward(), keys.left(), keys.right(), keys.jump(), false, keys.sprint());
        }
    }
}
