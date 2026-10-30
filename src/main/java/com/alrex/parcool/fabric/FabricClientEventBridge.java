package com.alrex.parcool.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import com.alrex.parcool.client.renderer.GrappleRopeRenderer;

import com.alrex.parcool.client.GrappleCameraHandler;
import com.alrex.parcool.client.GrappleTargetOverlay;
import com.alrex.parcool.client.animation.PassiveAnimationProcessor;
import com.alrex.parcool.client.animation.system.handle.AnimationSystemEventHandler;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.handlers.InputHandler;
import com.alrex.parcool.fabric.compat.event.ClientTickEvent;
import com.alrex.parcool.fabric.compat.event.ComputeFovModifierEvent;
import com.alrex.parcool.fabric.compat.event.InputEvent;
import com.alrex.parcool.fabric.compat.event.MovementInputUpdateEvent;
import com.alrex.parcool.fabric.compat.event.RenderFrameEvent;
import com.alrex.parcool.fabric.compat.event.RenderGuiEvent;
import com.alrex.parcool.fabric.compat.event.ViewportEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * Client half of {@link FabricEventBridge}: the handlers upstream's {@code ClientRegistrar}
 * subscribed on NeoForge, in the same order.
 */
@Environment(EnvType.CLIENT)
public final class FabricClientEventBridge {
    private FabricClientEventBridge() {
    }

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            var event = ClientTickEvent.Pre.INSTANCE;
            ParCoolKeyBinds.onTick(event);
            InputHandler.onTick(event);
            AnimationSystemEventHandler.onTick(event);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var event = ClientTickEvent.Post.INSTANCE;
            HUDRegistry.getInstance().onTick(event);
            GrappleCameraHandler.onClientTick(event);
        });
        HudElementRegistry.addLast(com.alrex.parcool.ParCool.resourceLocation("grapple_target"),
                (graphics, deltaTracker) -> GrappleTargetOverlay.onRenderGui(new RenderGuiEvent.Post(graphics, deltaTracker)));
        LevelRenderEvents.COLLECT_SUBMITS.register(GrappleRopeRenderer::onRenderLevel);
        // Upstream registers this listener with low priority, i.e. after ActionProcessor's.
        var passiveAnimations = new PassiveAnimationProcessor();
        FabricEventBridge.PLAYER_TICK_LATE.add(passiveAnimations::onTick);
    }

    /** From the mixin on {@code Minecraft#startAttack} / {@code #startUseItem}. */
    public static InputEvent.InteractionKeyMappingTriggered fireInteraction(
            InputEvent.InteractionKeyMappingTriggered.Kind kind, InteractionHand hand) {
        var event = new InputEvent.InteractionKeyMappingTriggered(kind, hand);
        InputHandler.onInput(event);
        return event;
    }

    /** From the mixin in {@code LocalPlayer#aiStep}, right after the input was read. */
    public static void fireMovementInput(Player player, ClientInput input) {
        InputHandler.onPlayerInputUpdated(new MovementInputUpdateEvent(player, input));
    }

    /** From the mixin at the end of {@code Camera#update}. */
    public static ViewportEvent.ComputeCameraAngles fireCameraAngles(Camera camera, double partialTick, float yaw, float pitch, float roll) {
        var event = new ViewportEvent.ComputeCameraAngles(camera, partialTick, yaw, pitch, roll);
        GrappleCameraHandler.onComputeCameraAngles(event);
        AnimationSystemEventHandler.onSetupCamera(event);
        return event;
    }

    public static float fireFov(Player player, float fovModifier) {
        var event = new ComputeFovModifierEvent(player, fovModifier);
        GrappleCameraHandler.onComputeFov(event);
        return event.getNewFovModifier();
    }

    /** From the mixin at the start of {@code GameRenderer#extract} (once per frame). */
    public static void fireRenderFrame(DeltaTracker deltaTracker) {
        AnimationSystemEventHandler.onRenderTick(new RenderFrameEvent.Pre(deltaTracker));
    }
}
