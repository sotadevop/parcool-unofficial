package com.alrex.parcool.api.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.IRequestable;
import com.alrex.parcool.common.network.ActionStatePacket;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.ParCoolEventBus;
import com.alrex.parcool.fabric.compat.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public abstract class Action {
	public Action(Parkourability parkourability, ActionEntry<? extends Action> entry) {
		this(parkourability, entry, null);
	}

	public Action(Parkourability parkourability, ActionEntry<? extends Action> entry, Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions) {
		this.entry = entry;
		this.parkourability = parkourability;
		this.exclusiveActions = exclusiveActions;
		if (parkourability.player().isLocalPlayer()) {
			var actionConfig = ParCool.getConfig().client().get(entry);
			if (actionConfig.instantInputType() != null) {
				input = actionConfig.instantInputType().get().newInput();
			} else if (actionConfig.continuousInputType() != null) {
				input = actionConfig.continuousInputType().get().newInput();
			} else {
				input = null;
			}
		} else {
			input = null;
		}
	}

	protected final Parkourability parkourability;
	protected final ActionEntry<? extends Action> entry;
	@Nullable
	protected final Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions;
	@Nullable
	protected final GeneralInputType.Input input;
	private int tickSinceStarted = 100;

	public int getTickSinceStarted() {
		return tickSinceStarted;
	}

	public ActionEntry<? extends Action> getEntry() {
		return entry;
	}

	public SynchronizedDataHolder getSynchronizedData() {
		return SynchronizedDataHolder.empty();
	}

	@Environment(EnvType.CLIENT)
	@Nullable
	public ParCoolKeyBinds.IStateProvider getKeyBind() {
		return null;
	}

	public void tick() {
		if (tickSinceStarted >= 0) {
			tickSinceStarted++;
		}
		if (input != null) {
			var key = getKeyBind();
			if (key != null) input.tick(key);
		}
		onTick();
		if (parkourability.player().level().isClientSide) {
			onTickInClient();
			if (parkourability.player().isLocalPlayer()) {
				onTickInLocalClient();
			} else {
				onTickInOtherClient();
			}
		} else {
			onTickInServer();
		}
	}

	public void start() {
		tickSinceStarted = 0;

		onStart();
		if (parkourability.player().isLocalPlayer()) {
			onStartInClient();
			onStartInLocalClient();
			takeCost(StaminaConsumption.Type.START);
		} else {
			if (parkourability.player().level().isClientSide()) {
				onStartInClient();
				onStartInOtherClient();
			} else {
				onStartInServer();
			}
		}
	}

	/// <strong>Warning</strong> : This method use internal system directly, without waiting system lifecycle.
	/// Therefore, the synchronization packet is not bundled with other packet, this cause increase of number of sent packet.
	/// And this can cause problems if used without being careful.
	///
	/// Please use <code>canStart</code> method as long as possible
	public void startExplicitly() {
		start();
		var player = parkourability.player();
		var clientSide = player.level().isClientSide();
        var packet = clientSide
				? ActionStateSetPacket.fromClient(parkourability.player().getUUID())
				: ActionStateSetPacket.fromServer(parkourability.player().getUUID());
		packet.add(new ActionStatePacket(
				entry.id().getNamespace(),
				Collections.singletonList(getSynchronizedData().packToEntry(ActionStatePacket.Type.START, entry))
		));
        if (clientSide) {
            PacketDistributor.sendToServer(packet);
        } else {
            PacketDistributor.sendToAllPlayers(packet);
        }
	}

	protected final boolean isPossible() {
		var player = parkourability.player();
		var option = entry.option();
		if (player.isSpectator()) return false;
		if (!option.availableWhileExhausted() && parkourability.getStamina().isExhausted()) return false;
		var currentPose = player.getPose();
		if ((!option.availableInFluid() && player.isInFluidType())
				|| (!option.availableNotInFluid() && !player.isInFluidType())
				|| (!option.availableWithFallFlying() && player.isFallFlying())
				|| (option.needOnGround() && !player.onGround())
				|| (option.needNotOnGround() && player.onGround())
				|| player.getAbilities().flying
				|| (!option.neededPoses().isEmpty() && option.neededPoses().stream().noneMatch(currentPose::equals))
				|| !parkourability.permit(entry)
		) {
			return false;
		}
		var parent = entry.parent();
		if (parent != null && !parkourability.get(parent).isDoing()) {
			return false;
		}
		if (exclusiveActions != null) {
			for (var exclusiveAction : exclusiveActions) {
				if (parkourability.get(exclusiveAction).isDoing()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isReadyToStart() {
		if (!isPossible()) return false;
        if (ParCoolEventBus.post(new ParCoolActionEvent.TryToStart(parkourability.player(), this)).isCanceled())
			return false;
		return (this instanceof IRequestable<?> requestable)
				? parkourability.canStartByRequest(requestable)
				: canStart();
	}

	protected void takeCost(StaminaConsumption.Type type) {
		if (parkourability.getStamina() instanceof AbstractLocalStamina stamina) {
			var cost = parkourability.getCost(entry, type);
			if (cost != 0) stamina.consume(cost);
		}
	}

	public abstract boolean canStart();

	public void onStart() {
	}

	public void onStartInServer() {
	}

	@Environment(EnvType.CLIENT)
	public void onStartInClient() {
	}

	@Environment(EnvType.CLIENT)
	public void onStartInOtherClient() {
	}

	@Environment(EnvType.CLIENT)
	public void onStartInLocalClient() {
	}

	public void onTick() {
	}

	public void onTickInServer() {
	}

	@Environment(EnvType.CLIENT)
	public void onTickInClient() {
	}

	@Environment(EnvType.CLIENT)
	public void onTickInLocalClient() {
	}

	@Environment(EnvType.CLIENT)
	public void onTickInOtherClient() {
	}

	@Environment(EnvType.CLIENT)
	public boolean wantsToShowStatusBar() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public float getStatusValue() {
		return 0;
	}
}
