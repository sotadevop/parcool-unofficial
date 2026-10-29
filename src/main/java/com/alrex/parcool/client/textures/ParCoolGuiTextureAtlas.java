package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;

@Environment(EnvType.CLIENT)
public class ParCoolGuiTextureAtlas extends TextureAtlasHolder {
    public static final Identifier TEXTURE_LOCATION = ParCool.resourceLocation("textures/atlas/gui.png");

    public static final Identifier BREWING_RECIPE_BOX = register("icon/brewing_recipe_box");
    public static final Identifier STAMINA_FULL = register("hud/stamina_full");
    public static final Identifier STAMINA_HALF = register("hud/stamina_half");
    public static final Identifier STAMINA_EMPTY = register("hud/stamina_empty");
    public static final Identifier STAMINA_EXHAUSTED_FULL = register("hud/stamina_exhausted_full");
    public static final Identifier STAMINA_EXHAUSTED_HALF = register("hud/stamina_exhausted_half");
    public static final Identifier STAMINA_EXHAUSTED_EMPTY = register("hud/stamina_exhausted_empty");
    public static final Identifier STAMINA_INEXHAUSTIBLE_FULL = register("hud/stamina_inexhaustible_full");
    public static final Identifier STAMINA_INEXHAUSTIBLE_HALF = register("hud/stamina_inexhaustible_half");
    public static final Identifier STAMINA_INEXHAUSTIBLE_EMPTY = register("hud/stamina_inexhaustible_empty");
    public static final Identifier STAMINA_BRIGHT_FULL = register("hud/stamina_bright_full");
    public static final Identifier STAMINA_BRIGHT_HALF = register("hud/stamina_bright_half");
    public static final Identifier STAMINA_BRIGHT_EMPTY = register("hud/stamina_bright_empty");
    public static final Identifier STAMINA_FLUSH = register("hud/stamina_flush");
    public static final Identifier SKILLTREE_ACTION_UNAVAILABLE = register("icon/action_frame_unavailable");
    public static final Identifier SKILLTREE_ACTION_UNLOCKED = register("icon/action_frame_unlocked");
    public static final Identifier SKILLTREE_ACTION_LOCKED = register("icon/action_frame_locked");
    public static final Identifier BASIC_BUTTON_INACTIVE = register("icon/basic_button_inactive");
    public static final Identifier BASIC_BUTTON_OFF = register("icon/basic_button_off");
    public static final Identifier BASIC_BUTTON_OFF_HOVER = register("icon/basic_button_off_hover");
    public static final Identifier BASIC_BUTTON_ON = register("icon/basic_button_on");
    public static final Identifier BASIC_BUTTON_ON_HOVER = register("icon/basic_button_on_hover");
    public static final Identifier SETTING_BUTTON = register("icon/setting_button");
    public static final Identifier SETTING_BUTTON_HOVER = register("icon/setting_button_hover");
    public static final Identifier BASIC_SWITCH_INACTIVE = register("icon/basic_switch_inactive");
    public static final Identifier BASIC_SWITCH_OFF = register("icon/basic_switch_off");
    public static final Identifier BASIC_SWITCH_OFF_HOVER = register("icon/basic_switch_off_hover");
    public static final Identifier BASIC_SWITCH_ON = register("icon/basic_switch_on");
    public static final Identifier BASIC_SWITCH_ON_HOVER = register("icon/basic_switch_on_hover");
    public static final Identifier SETTINGS_ICON_ACTIONS = register("icon/settings_actions");
    public static final Identifier SETTINGS_ICON_CONTROLS = register("icon/settings_controls");
    public static final Identifier SETTINGS_ICON_ANIMATIONS = register("icon/settings_animations");
    public static final Identifier SETTINGS_ICON_OPTIONS = register("icon/settings_options");
    public static final Identifier SETTINGS_ICON_LINKS = register("icon/settings_links");
    public static final Identifier EXPERIENCE_BOX = register("icon/experience_box");
    public static final Identifier EXPERIENCE_BOX_OVERLAY = register("icon/experience_box_overlay");
    public static final Identifier BUTTON_BACK = register("icon/button_back");
    public static final Identifier BUTTON_CLOSE = register("icon/button_close");
    public static final Identifier BUTTON_HAMBURGER = register("icon/button_hamburger");
    public static final Identifier BUTTON_HOME = register("icon/button_home");
    public static final Identifier BUTTON_EXPAND = register("icon/button_expand");
    public static final Identifier BUTTON_SHRINK = register("icon/button_shrink");

    public ParCoolGuiTextureAtlas(TextureManager manager) {
        super(manager, TEXTURE_LOCATION, ParCool.resourceLocation("gui_sprites"));
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getSprite(@Nonnull Identifier spriteId) {
        return super.getSprite(spriteId);
    }

    private static Identifier register(String path) {
        return ParCool.resourceLocation(path);
    }
}
