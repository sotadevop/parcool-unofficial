package com.alrex.parcool.api.action;

import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.fabric.compat.event.Event;

public class RegisterParCoolActionEvent extends Event {
    private final ActionRegistry registry;

    public RegisterParCoolActionEvent(ActionRegistry registry) {
        this.registry = registry;
    }

    public void register(ActionGroup group) {
        registry.register(group);
    }
}
