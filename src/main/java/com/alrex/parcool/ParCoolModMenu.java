package com.alrex.parcool;

import com.alrex.parcool.client.gui.screen.ParCoolSettingScreen;
import com.alrex.parcool.common.Parkourability;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;

/**
 * Mod Menu's "Configure" button. ParCool's settings screen edits the player's live parkour state,
 * so it only opens in a world; on the title screen a note explains that.
 */
public class ParCoolModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var minecraft = Minecraft.getInstance();
            var player = minecraft.player;
            var parkourability = player == null ? null : Parkourability.get(player);
            if (parkourability != null) return new ParCoolSettingScreen(parkourability);
            return new AlertScreen(() -> minecraft.setScreen(parent),
                    Component.translatable("parcool.modmenu.title"),
                    Component.translatable("parcool.modmenu.need_world"));
        };
    }
}
