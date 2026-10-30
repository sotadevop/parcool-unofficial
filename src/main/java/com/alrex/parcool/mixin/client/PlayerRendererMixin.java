package com.alrex.parcool.mixin.client;

import org.joml.Matrix4f;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolGeneralEquipmentLayer;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.alrex.parcool.fabric.compat.IAvatarRenderStateEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<Avatar, AvatarRenderState, PlayerModel> {
	public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
		super(context, model, shadowRadius);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(EntityRendererProvider.Context context, boolean slim, CallbackInfo ci) {
		this.addLayer(new ParCoolGeneralEquipmentLayer(this,
				() -> context.bakeLayer(slim ? ParCoolModelLayers.INNER_EQUIPMENT_SLIM : ParCoolModelLayers.INNER_EQUIPMENT),
				() -> context.bakeLayer(slim ? ParCoolModelLayers.OUTER_EQUIPMENT_SLIM : ParCoolModelLayers.OUTER_EQUIPMENT)
		));
	}

	// Upstream kept a swimming blend in an `if (true) ... else` branch that never ran; only the live
	// branch is ported.
	@Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupRotationsHead(AvatarRenderState state, PoseStack stack, float bodyRot, float scale, CallbackInfo ci) {
		if (state.isFallFlying) return;
		if (((IAvatarRenderStateEntity) state).parcool$getPlayer() instanceof IPlayerAnimatorHolder holder) {
			var animator = holder.getParCoolPlayerAnimator();
			if (animator.cancelPlayerModelRotation()) return;

			var transform = animator.getCurrentTransformation();
			if (transform == null) return;

			var bodyTransformation = transform.transformation().transforms().get(AnimatableModelPart.BODY);
			if (bodyTransformation == null) return;

			if (!transform.isOverwriting()) {
				bodyTransformation = Transform.NO_TRANSFORMATION.morph(bodyTransformation, transform.blendFactor(), true);
			}
			var translation = bodyTransformation.translation();
			// 26.x PoseStack#mulPose takes a matrix, not a quaternion.
			stack.mulPose(new Matrix4f().rotation(MathUtil.rotation(Vec3f.YP, (float) Math.toRadians(180.0F - bodyRot))));

			stack.translate(translation.x(), translation.y(), translation.z());
			stack.translate(0, 0.9, 0);
			stack.mulPose(new Matrix4f().rotation(new Quaternionf(bodyTransformation.rotation()).normalize()));
			stack.translate(0, -0.9, 0);
			ci.cancel();
		}
	}
}
