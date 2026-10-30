package com.alrex.parcool.client.renderer.blockentity;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.renderer.RenderTypes;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws the ropes of a zipline hook.
 *
 * <p>26.x renders in two steps: {@link #extractRenderState} copies what is needed from the block
 * entity (the level is only accessible there, so the light of each rope segment is sampled then),
 * and {@link #submit} emits the geometry through {@link SubmitNodeCollector#submitCustomGeometry}.
 * The vertex maths is upstream's.
 */
@Environment(EnvType.CLIENT)
public class ZiplineHookRenderer implements BlockEntityRenderer<ZiplineHookTileEntity, ZiplineHookRenderer.State> {
    public static final Identifier TEXTURE_LOCATION = ParCool.resourceLocation("textures/misc/zipline.png");

    public static class State extends BlockEntityRenderState {
        final List<Rope> ropes = new ArrayList<>();
    }

    record Rope(Zipline zipline, Vec3 offset, int[] segmentLights) {
    }

    public ZiplineHookRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ZiplineHookTileEntity entity, State state, float partialTick, Vec3 cameraPos,
                                   @Nullable ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumbling);
        state.ropes.clear();
        var level = entity.getLevel();
        if (level == null) return;
        Vec3 hookPoint = entity.getHookPoint();
        BlockPos hookBlockPos = entity.getBlockPos();
        var offset = new Vec3(hookPoint.x() - hookBlockPos.getX(), hookPoint.y() - hookBlockPos.getY(), hookPoint.z() - hookBlockPos.getZ());
        var iterator = entity.getRenderAbleZiplines();
        while (iterator.hasNext()) {
            var zipline = iterator.next();
            int divisionCount = divisionCount(zipline);
            int[] lights = new int[divisionCount];
            for (int i = 0; i < divisionCount; i++) {
                lights[i] = zipline.powered() ? 15728880 /* full bright, as upstream */ : segmentLight(level, hookBlockPos, zipline.shape(), i, divisionCount);
            }
            state.ropes.add(new Rope(zipline, offset, lights));
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        for (var rope : state.ropes) {
            poseStack.pushPose();
            poseStack.translate(rope.offset().x(), rope.offset().y(), rope.offset().z());
            collector.submitCustomGeometry(poseStack, RenderTypes.ZIPLINE_3D, (pose, vertexConsumer) -> renderRope(rope, pose.pose(), vertexConsumer));
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public boolean shouldRender(ZiplineHookTileEntity entity, Vec3 viewPoint) {
        return !entity.getConnectionPoints().isEmpty();
    }

    private static int divisionCount(Zipline zipline) {
        return Math.max(Mth.ceil(zipline.shape().getLength()), 2);
    }

    /** Upstream's light lookup (light at the middle of the segment), done during extraction. */
    private static int segmentLight(Level level, BlockPos pos, ZiplineShape shape, int currentCount, int maxCount) {
        Vec3 from = shape.getMidPointOffsetFromStart((float) currentCount / maxCount);
        Vec3 to = shape.getMidPointOffsetFromStart((float) (currentCount + 1) / maxCount);
        var midBlockPos = new BlockPos(
                Mth.floor(pos.getX() + 0.5f + (from.x + to.x) / 2.),
                Mth.floor(pos.getY() + 0.5f + (from.y + to.y) / 2.),
                Mth.floor(pos.getZ() + 0.5f + (from.z + to.z) / 2.)
        );
        return LightCoordsUtil.pack(level.getBrightness(LightLayer.BLOCK, midBlockPos), level.getBrightness(LightLayer.SKY, midBlockPos));
    }

    private static void renderRope(Rope rope, Matrix4f transformMatrix, VertexConsumer vertexConsumer) {
        var zipline = rope.zipline();
        int color = zipline.info().color();
        float r = ((0xFF0000 & color) >> 16) / 255f;
        float g = ((0x00FF00 & color) >> 8) / 255f;
        float b = (0x0000FF & color) / 255f;
        Vec3 endOffsetFromStart = zipline.shape().getOffsetFromStartToEnd();

        int divisionCount = rope.segmentLights().length;
        float invLengthSqrtXZ = (float) Mth.invSqrt(endOffsetFromStart.x() * endOffsetFromStart.x() + endOffsetFromStart.z() * endOffsetFromStart.z());
        float unitLengthX = (float) (endOffsetFromStart.x() * invLengthSqrtXZ);
        float unitLengthZ = (float) (endOffsetFromStart.z() * invLengthSqrtXZ);

        for (int i = 0; i < divisionCount; i++) {
            renderRopeSingleBlock3D(
                    transformMatrix,
                    vertexConsumer,
                    zipline.shape(),
                    i, divisionCount,
                    unitLengthX, unitLengthZ,
                    r, g, b,
                    zipline.powered(),
                    rope.segmentLights()[i]
            );
        }
    }

    private static void renderRopeSingleBlock3D(
            Matrix4f transformMatrix,
            VertexConsumer vertexConsumer,
            ZiplineShape zipline,
            int currentCount, int maxCount,
            float unitLengthX,
            float unitLengthZ,
            float r, float g, float b,
            boolean powered,
            int light
    ) {
        Vector3f[] vertexList = new Vector3f[8];
        Vec3[] midPoints = new Vec3[2];
        for (int i = 0; i < 2; i++) {
            float phase = (float) (currentCount + i) / maxCount;

            Vec3 midPointD = midPoints[i] = zipline.getMidPointOffsetFromStart(phase);
            Vector3f midPoint = new Vector3f((float) midPointD.x(), (float) midPointD.y(), (float) midPointD.z());

            final float width = 0.09375f;
            float tilt = zipline.getSlope(phase);
            float tiltInv = Mth.invSqrt(tilt * tilt + 1);
            float yOffset = width * tiltInv / 1.41421356f /*sqrt(2)*/;
            float xBaseOffset = unitLengthX * width * tilt * tiltInv / 1.41421356f;
            float zBaseOffset = unitLengthZ * width * tilt * tiltInv / 1.41421356f;
            float xOffset = unitLengthZ * width / 1.41421356f;
            float zOffset = -unitLengthX * width / 1.41421356f;
            vertexList[4 * i] = new Vector3f(
                    (midPoint.x() - xBaseOffset + xOffset),
                    (midPoint.y() + yOffset),
                    (midPoint.z() - zBaseOffset + zOffset)
            );
            vertexList[4 * i + 1] = new Vector3f(
                    (midPoint.x() - xBaseOffset - xOffset),
                    (midPoint.y() + yOffset),
                    (midPoint.z() - zBaseOffset - zOffset)
            );
            vertexList[4 * i + 2] = new Vector3f(
                    (midPoint.x() + xBaseOffset - xOffset),
                    (midPoint.y() - yOffset),
                    (midPoint.z() + zBaseOffset - zOffset)
            );
            vertexList[4 * i + 3] = new Vector3f(
                    (midPoint.x() + xBaseOffset + xOffset),
                    (midPoint.y() - yOffset),
                    (midPoint.z() + zBaseOffset + zOffset)
            );
        }
        // Render side planes
        for (int i = 0; i < 4; i++) {
            vertexConsumer
                    .addVertex(transformMatrix, vertexList[i].x(), vertexList[i].y(), vertexList[i].z())
                    .setColor(r, g, b, 1f).setUv(0, (i + (powered ? 4 : 0)) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer
                    .addVertex(transformMatrix, vertexList[(i + 1) % 4].x(), vertexList[(i + 1) % 4].y(), vertexList[(i + 1) % 4].z())
                    .setColor(r, g, b, 1f).setUv(0, (i + (powered ? 5 : 1)) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer
                    .addVertex(transformMatrix, vertexList[4 + (i + 1) % 4].x(), vertexList[4 + (i + 1) % 4].y(), vertexList[4 + (i + 1) % 4].z())
                    .setColor(r, g, b, 1f).setUv(1, (i + (powered ? 5 : 1)) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer
                    .addVertex(transformMatrix, vertexList[4 + i].x(), vertexList[4 + i].y(), vertexList[4 + i].z())
                    .setColor(r, g, b, 1f).setUv(1, (i + (powered ? 4 : 0)) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
        }

        // Render caps
        if (currentCount == 0) {
            vertexConsumer.addVertex(transformMatrix, vertexList[3].x(), vertexList[3].y(), vertexList[3].z()).setColor(r, g, b, 1f).setUv(0, (powered ? 4 : 0) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(transformMatrix, vertexList[2].x(), vertexList[2].y(), vertexList[2].z()).setColor(r, g, b, 1f).setUv(0, (powered ? 5 : 1) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(transformMatrix, vertexList[1].x(), vertexList[1].y(), vertexList[1].z()).setColor(r, g, b, 1f).setUv(1f / 16f, (powered ? 5 : 1) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(transformMatrix, vertexList[0].x(), vertexList[0].y(), vertexList[0].z()).setColor(r, g, b, 1f).setUv(1f / 16f, (powered ? 4 : 0) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
        } else if (currentCount == maxCount - 1) {
            vertexConsumer.addVertex(transformMatrix, vertexList[4].x(), vertexList[4].y(), vertexList[4].z()).setColor(r, g, b, 1f).setUv(15f / 16f, (powered ? 4 : 0) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(transformMatrix, vertexList[5].x(), vertexList[5].y(), vertexList[5].z()).setColor(r, g, b, 1f).setUv(15f / 16f, (powered ? 5 : 1) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(transformMatrix, vertexList[6].x(), vertexList[6].y(), vertexList[6].z()).setColor(r, g, b, 1f).setUv(1f, (powered ? 5 : 1) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(transformMatrix, vertexList[7].x(), vertexList[7].y(), vertexList[7].z()).setColor(r, g, b, 1f).setUv(1f, (powered ? 4 : 0) / 8f).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(0f, 1f, 0f);
        }
    }
}
