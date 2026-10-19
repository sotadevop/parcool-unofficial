package com.alrex.parcool;

import com.alrex.parcool.client.gui.ParCoolSettingScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Mod Menu integration: the "Configure" button in Mod Menu's mod list opens ParCool's own settings
 * screen (the same one Alt + P opens) and returns to the mod list when it is closed.
 * Only loaded when Mod Menu is installed, because it is registered as Mod Menu's entrypoint.
 */
public class ParCoolModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ParCoolSettingScreen::createFromMenu;
    }
}
