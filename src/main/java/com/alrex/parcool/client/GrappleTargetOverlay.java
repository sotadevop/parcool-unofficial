package com.alrex.parcool.client;

import org.joml.Matrix4f;

import net.minecraft.client.renderer.RenderPipelines;
import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.alrex.parcool.fabric.compat.event.RenderGuiEvent;
import org.joml.Quaternionf;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class GrappleTargetOverlay {
    public static final Identifier TEXTURE = ParCool.resourceLocation("textures/misc/grapple_target.png");
    private static final int TEXTURE_SIZE = 15;

    private static float screenX;
    private static float screenY;

    /**
     * Upstream projected the target in RenderLevelStageEvent and drew it in the HUD. 26.x's camera
     * exposes the combined view/projection matrix, so both happen in the HUD pass; the HUD is not
     * drawn while it is hidden (F1), which replaces upstream's hideGui check.
     */
    private static boolean project(Minecraft minecraft) {
        if (minecraft.player == null) return false;
        Parkourability parkourability = Parkourability.get(minecraft.player);
        if (parkourability == null) return false;
        Vec3 target = parkourability.get(ParCoolActions.GRAPPLE).getPreviewTarget();
        if (target == null) return false;
        var camera = minecraft.getEntityRenderDispatcher().camera;
        if (camera == null) return false;

        Vec3 cameraPos = camera.position();
        Vector4f point = new Vector4f(
                (float) (target.x - cameraPos.x),
                (float) (target.y - cameraPos.y),
                (float) (target.z - cameraPos.z),
                1f
        );
        point.mul(camera.getViewRotationProjectionMatrix(new Matrix4f()));
        if (point.w() <= 1.0e-4) return false;

        float normalisedX = point.x() / point.w();
        float normalisedY = point.y() / point.w();
        if (Math.abs(normalisedX) > 1.5f || Math.abs(normalisedY) > 1.5f) return false;

        var window = minecraft.getWindow();
        screenX = (normalisedX * 0.5f + 0.5f) * window.getGuiScaledWidth();
        screenY = (1 - (normalisedY * 0.5f + 0.5f)) * window.getGuiScaledHeight();
        return true;
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!project(Minecraft.getInstance())) return;
        int size = ParCool.getConfig().client().targetIndicatorSize.get();


        event.getGuiGraphics().blit(RenderPipelines.GUI_TEXTURED, TEXTURE, Math.round(screenX - size / 2f), Math.round(screenY - size / 2f), 0, 0, size, size, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);

    }
}
