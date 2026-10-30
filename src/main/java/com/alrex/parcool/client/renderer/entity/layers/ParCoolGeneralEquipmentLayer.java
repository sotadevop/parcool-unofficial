package com.alrex.parcool.client.renderer.entity.layers;

import com.alrex.parcool.client.renderer.entity.ParCoolModelPoser;
import com.alrex.parcool.common.item.armor.EquipAble;

import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.fabric.compat.IAvatarRenderStateEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Draws ParCool equipment (traceur gloves and boots) on players. 26.x layers only get a render
 * state; the player comes from IAvatarRenderStateEntity. Known difference to upstream: no
 * enchantment glint on the equipment model.
 */
@Environment(EnvType.CLIENT)
public class ParCoolGeneralEquipmentLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    // One model per slot and layer, each baked separately (as vanilla's armor layer does): submits are
    // drawn later, so models must not share parts whose visibility differs per slot.
    private final Map<EquipmentSlot, EquipmentModel> innerModels = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, EquipmentModel> outerModels = new EnumMap<>(EquipmentSlot.class);

    public ParCoolGeneralEquipmentLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent, Supplier<ModelPart> innerLayer, Supplier<ModelPart> outerLayer) {
        super(parent);
        for (var slot : EquipmentSlot.values()) {
            innerModels.put(slot, new EquipmentModel(innerLayer.get(), slot));
            outerModels.put(slot, new EquipmentModel(outerLayer.get(), slot));
        }
    }

    @Override
    public void submit(@Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, int packedLight, @Nonnull AvatarRenderState state, float yRot, float xRot) {
        var entity = ((IAvatarRenderStateEntity) state).parcool$getPlayer();
        if (entity == null || !shouldRenderModel(entity)) return;
        for (var slot : EquipmentSlot.values()) {
            var itemStack = entity.getItemBySlot(slot);
            if (!(itemStack.getItem() instanceof EquipAble equipAble)) continue;
            for (var layer : EquipmentRenderLayer.values()) {
                renderEquipment(poseStack, collector, equipAble, itemStack, entity, state, slot, layer, packedLight);
            }
        }
        if (AdditionalMods.curios().isInstalled()) {
            AdditionalMods.curios().getRenderAbleGeneralEquipments(entity).forEach(itemStack -> {
                if (!(itemStack.getItem() instanceof EquipAble equipAble)) return;
                var slot = equipAble.getEquipmentSlot();
                for (var layer : EquipmentRenderLayer.values()) {
                    renderEquipment(poseStack, collector, equipAble, itemStack, entity, state, slot, layer, packedLight);
                }
            });
        }
    }

    private void renderEquipment(PoseStack poseStack, SubmitNodeCollector collector, EquipAble equipAble, ItemStack itemStack,
                                 LivingEntity entity, AvatarRenderState state, EquipmentSlot slot, EquipmentRenderLayer layer, int light) {
        var texture = equipAble.getEquipmentTexture(entity, slot, layer);
        if (texture == null) return;
        var model = switch (layer) {
            case INNER -> innerModels.get(slot);
            case OUTER -> outerModels.get(slot);
        };
        int color = equipAble.hasCustomEquipmentColor() ? equipAble.getCustomEquipmentColor(itemStack) : ~0;
        model.setupAnim(state);
        renderColoredCutoutModel(model, texture, poseStack, collector, light, state, color, 0);
    }

    static void setPartVisibility(HumanoidModel<?> model, EquipmentSlot slot) {
        for (var part : model.allParts()) part.visible = false;
        model.root().visible = true;
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST, MAINHAND, OFFHAND -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }

    private boolean shouldRenderModel(Player player) {
        return !AdditionalMods.epicFight().isBattleMode(player);
    }

    /** Poses itself with ParCool's animation, like the player model it is drawn over. */
    private static final class EquipmentModel extends HumanoidModel<AvatarRenderState> {
        private final EquipmentSlot slot;

        EquipmentModel(ModelPart root, EquipmentSlot slot) {
            super(root);
            this.slot = slot;
        }

        @Override
        public void setupAnim(@Nonnull AvatarRenderState state) {
            ParCoolModelPoser.setupAnim(this, state, () -> super.setupAnim(state));
            setPartVisibility(this, slot);
        }
    }
}
