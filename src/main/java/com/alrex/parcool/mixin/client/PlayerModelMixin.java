package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.renderer.entity.ParCoolModelPoser;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Replaces the vanilla limb pose with ParCool's animation (see ParCoolModelPoser). In 26.x the hat,
 * sleeves and pants are child parts, so they follow head and limbs without upstream's hat.copyFrom.
 */
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin extends HumanoidModel<AvatarRenderState> {
	public PlayerModelMixin(ModelPart root) {
		super(root);
	}

	@Redirect(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"))
	protected void onSetupAnimHead(HumanoidModel<?> model, HumanoidRenderState humanoidState) {
		var state = (AvatarRenderState) humanoidState;
		ParCoolModelPoser.setupAnim(this, state, () -> super.setupAnim(state));
	}
}
