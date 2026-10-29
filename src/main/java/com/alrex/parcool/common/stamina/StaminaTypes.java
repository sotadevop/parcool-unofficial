package com.alrex.parcool.common.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import com.alrex.parcool.common.stamina.impl.HungerStamina;
import com.alrex.parcool.common.stamina.impl.NoneStamina;
import com.alrex.parcool.common.stamina.impl.ParCoolStamina;
import net.minecraft.resources.Identifier;

public class StaminaTypes {
    private static final Identifier NONE_ID = ParCool.resourceLocation("none");
    private static final Identifier PARCOOL_ID = ParCool.resourceLocation("parcool");
    private static final Identifier HUNGER_ID = ParCool.resourceLocation("hunger");

    public static final StaminaTypeEntry<NoneStamina> NONE_STAMINA = new StaminaTypeEntry<>(NONE_ID, NONE_ID.getPath(), NoneStamina::new);
    public static final StaminaTypeEntry<ParCoolStamina> PARCOOL_STAMINA = new StaminaTypeEntry<>(PARCOOL_ID, PARCOOL_ID.getPath(), ParCoolStamina::new);
    public static final StaminaTypeEntry<HungerStamina> HUNGER_STAMINA = new StaminaTypeEntry<>(HUNGER_ID, HUNGER_ID.getPath(), HungerStamina::new);

    public static void onRegister(RegisterParCoolStaminaTypeEvent event) {
        event.register(NONE_STAMINA);
        event.register(PARCOOL_STAMINA);
        event.register(HUNGER_STAMINA);
    }
}
