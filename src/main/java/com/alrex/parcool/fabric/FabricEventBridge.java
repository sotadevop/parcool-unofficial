package com.alrex.parcool.fabric;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.handlers.AddAttributesHandler;
import com.alrex.parcool.common.handlers.PlayerEventHandler;
import com.alrex.parcool.fabric.compat.event.EntityAttributeModificationEvent;
import com.alrex.parcool.fabric.compat.event.EntityEvent;
import com.alrex.parcool.fabric.compat.event.LevelTickEvent;
import com.alrex.parcool.fabric.compat.event.LivingDamageEvent;
import com.alrex.parcool.fabric.compat.event.LivingEvent;
import com.alrex.parcool.fabric.compat.event.LivingFallEvent;
import com.alrex.parcool.fabric.compat.event.PlayerEvent;
import com.alrex.parcool.fabric.compat.event.PlayerTickEvent;
import com.alrex.parcool.fabric.compat.event.RegisterCommandsEvent;
import com.alrex.parcool.server.command.CommandRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Connects ParCool's (upstream, NeoForge-style) event handlers to Fabric.
 *
 * <p>Events that Fabric API provides are subscribed in {@link #init()}. The others are fired by
 * ParCool's Fabric mixins ({@code com.alrex.parcool.mixin.fabric}) through the static
 * {@code fire*} methods. Each method calls the same handlers, in the same order, as upstream's
 * {@code CommonRegistrar} subscribed them on NeoForge. Client-only handlers are in
 * {@link FabricClientEventBridge}.
 */
public final class FabricEventBridge {
    /** Extra player tick listeners added by the client (upstream's PassiveAnimationProcessor). */
    static final List<Consumer<PlayerTickEvent.Post>> PLAYER_TICK_LATE = new CopyOnWriteArrayList<>();

    private FabricEventBridge() {
    }

    public static void init() {
        ServerTickEvents.END_LEVEL_TICK.register(FabricEventBridge::fireLevelTick);
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
                PlayerEventHandler.onClone(new PlayerEvent.Clone(newPlayer, oldPlayer, !alive)));
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) ->
                PlayerEventHandler.onChangeDimension(new PlayerEvent.PlayerChangedDimensionEvent(player)));
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) ->
                CommandRegistry.onRegisterCommand(new RegisterCommandsEvent(dispatcher)));
    }

    /** Server levels only: upstream's level tick handler returns immediately on the client. */
    private static void fireLevelTick(Level level) {
        ParCool.getActionProcessor().onTickLevel(new LevelTickEvent.Post(level));
    }

    /** From the mixin at the end of {@code Player#tick}, on both logical sides. */
    public static void firePlayerTick(Player player) {
        var event = new PlayerTickEvent.Post(player);
        ParCool.getActionProcessor().onTickPlayer(event);
        for (var listener : PLAYER_TICK_LATE) listener.accept(event);
    }

    /** @return the event; canceled means "no fall damage". */
    public static LivingFallEvent fireFall(LivingEntity entity, float distance, float damageMultiplier) {
        var event = new LivingFallEvent(entity, distance, damageMultiplier);
        PlayerEventHandler.onFall(event);
        return event;
    }

    public static void fireJump(LivingEntity entity) {
        PlayerEventHandler.onJump(new LivingEvent.LivingJumpEvent(entity));
    }

    public static double fireVisibility(LivingEntity entity, double visibility) {
        var event = new LivingEvent.LivingVisibilityEvent(entity, visibility);
        PlayerEventHandler.onLivingVisibilityEvent(event);
        return event.getVisibilityModifier();
    }

    public static float fireDamagePre(LivingEntity entity, DamageSource source, float damage) {
        var event = new LivingDamageEvent.Pre(entity, source, damage);
        PlayerEventHandler.onAttack(event);
        return event.getNewDamage();
    }

    public static EntityDimensions fireSize(Entity entity, EntityDimensions size) {
        var event = new EntityEvent.Size(entity, size);
        PlayerEventHandler.onSizeRefresh(event);
        return event.getNewSize();
    }

    /** From the mixin on {@code Player#createAttributes}. */
    public static void firePlayerAttributes(AttributeSupplier.Builder builder) {
        AddAttributesHandler.onAddAttributes(new EntityAttributeModificationEvent(EntityTypes.PLAYER, builder));
    }
}
