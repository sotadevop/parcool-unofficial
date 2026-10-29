package com.alrex.parcool.client.animation.system.event;

import com.alrex.parcool.client.animation.system.registration.AnimationProgresses;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.BlendingFactors;
import com.alrex.parcool.client.animation.system.registration.CodedAnimationComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.Event;

@Environment(EnvType.CLIENT)
public class RegisterAnimationEntryEvent extends Event {
    public RegisterAnimationEntryEvent() {
    }

    public void finish() {
        CodedAnimationComponents.getInstance().freeze();
        BlendingFactors.getInstance().freeze();
        AnimationProgresses.getInstance().freeze();
        AnimationSets.getInstance().freeze();
    }
}
