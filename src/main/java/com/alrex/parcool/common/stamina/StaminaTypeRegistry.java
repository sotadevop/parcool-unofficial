package com.alrex.parcool.common.stamina;

import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.api.stamina.IStaminaProvider;
import com.alrex.parcool.api.stamina.StaminaTypeEntry;
import com.alrex.parcool.common.BasicRegistry;
import net.minecraft.resources.Identifier;

import java.util.Collection;

public class StaminaTypeRegistry extends BasicRegistry<Identifier, StaminaTypeEntry<? extends AbstractLocalStamina>> {
    public <T extends AbstractLocalStamina> void register(StaminaTypeEntry<T> entry) {
        register(entry.id(), entry);
    }

    public boolean isRegistered(Identifier location) {
        return getRegistry().containsKey(location);
    }

    public IStaminaProvider<?> getProvider(Identifier id) {
        return getRegistry().getOrDefault(id, StaminaTypes.PARCOOL_STAMINA).provider();
    }

    public Collection<StaminaTypeEntry<?>> getEntries() {
        return getRegistry().values();
    }
}
