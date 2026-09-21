package com.logicalgeekboy.logical_zoom.mixin;

import com.logicalgeekboy.logical_zoom.LogicalZoom;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class LogicalZoomMixin {

    @ModifyVariable(
            method = "renderLevel",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private Matrix4f logicalZoom$modifyProjectionMatrix(Matrix4f projectionMatrix) {
        LogicalZoom.manageSmoothCamera();

        if (LogicalZoom.isZooming()) {
            float scale = 1.0f / LogicalZoom.zoomLevel;
            projectionMatrix.scale(scale, scale, 1.0f);
        }

        return projectionMatrix;
    }

    @WrapWithCondition(
            method = "renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lcom/mojang/renderpearl/api/textures/GpuTextureView;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/FirstPersonHandsAndItemsRenderer;submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V"
            )
    )
    private boolean logicalZoom$hideHandsWhenZooming(
            FirstPersonHandsAndItemsRenderer firstPersonHandsAndItemsRenderer,
            float deltaPartialTick,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            PlayerRenderState playerRenderState,
            FirstPersonHandsAndItemsRenderState handsAndItemsRenderState
    ) {
        return !(LogicalZoom.isZooming()
                && Minecraft.getInstance().options.getCameraType().isFirstPerson());
    }
}