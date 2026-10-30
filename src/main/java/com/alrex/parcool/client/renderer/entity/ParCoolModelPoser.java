package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.fabric.compat.IAvatarRenderStateEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

/**
 * ParCool's limb posing, shared by the player model (PlayerModelMixin) and the equipment models
 * (ParCoolGeneralEquipmentLayer). Upstream copied the posed player model onto the equipment model
 * with copyPropertiesTo; 26.x re-runs setupAnim on every model right before it is drawn, which would
 * overwrite a copied pose, so both models pose themselves from the render state instead.
 */
@Environment(EnvType.CLIENT)
public final class ParCoolModelPoser {
    private ParCoolModelPoser() {
    }

    /**
     * @param vanillaSetup runs the model's own (vanilla) setupAnim
     */
    public static void setupAnim(HumanoidModel<?> model, AvatarRenderState state, Runnable vanillaSetup) {
        if (state.isFallFlying || state.isPassenger
                || !(((IAvatarRenderStateEntity) state).parcool$getPlayer() instanceof IPlayerAnimatorHolder holder)) {
            vanillaSetup.run();
            return;
        }
        var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
        for (var part : model.allParts()) part.resetPose();
        if (transform == null) {
            vanillaSetup.run();
            return;
        }
        var transforms = transform.transformation().transforms();
        if (transform.isOverwriting()) {
            apply(transforms.get(AnimatableModelPart.HEAD), model.head);
            apply(transforms.get(AnimatableModelPart.RIGHT_ARM), model.rightArm);
            apply(transforms.get(AnimatableModelPart.RIGHT_LEG), model.rightLeg);
            apply(transforms.get(AnimatableModelPart.LEFT_ARM), model.leftArm);
            apply(transforms.get(AnimatableModelPart.LEFT_LEG), model.leftLeg);
        } else {
            vanillaSetup.run();
            float blend = transform.blendFactor();
            blend(transforms.get(AnimatableModelPart.HEAD), model.head, blend, true);
            blend(transforms.get(AnimatableModelPart.RIGHT_ARM), model.rightArm, blend, false);
            blend(transforms.get(AnimatableModelPart.RIGHT_LEG), model.rightLeg, blend, false);
            blend(transforms.get(AnimatableModelPart.LEFT_ARM), model.leftArm, blend, false);
            blend(transforms.get(AnimatableModelPart.LEFT_LEG), model.leftLeg, blend, false);
            Transform.NO_TRANSFORMATION.applyInQuaternion(model.body, blend, true);
        }
    }

    private static void apply(Transform transform, ModelPart part) {
        if (transform != null) transform.apply(part);
    }

    private static void blend(Transform transform, ModelPart part, float factor, boolean flag) {
        if (transform != null) transform.applyInQuaternion(part, factor, flag);
    }
}
