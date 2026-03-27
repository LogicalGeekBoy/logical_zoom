package com.logicalgeekboy.logical_zoom.mixin;

import com.logicalgeekboy.logical_zoom.LogicalZoom;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
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
            method = "renderItemInHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V"
            )
    )
    private boolean logicalZoom$hideHandsWhenZooming(
            ItemInHandRenderer itemInHandRenderer,
            float deltaPartialTick,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            LocalPlayer player,
            int packedLight
    ) {
        return !(LogicalZoom.isZooming()
                && Minecraft.getInstance().options.getCameraType().isFirstPerson());
    }
}