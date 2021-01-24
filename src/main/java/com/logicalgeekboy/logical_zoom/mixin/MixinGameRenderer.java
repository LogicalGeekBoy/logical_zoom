package com.logicalgeekboy.logical_zoom.mixin;

import com.logicalgeekboy.logical_zoom.render.ZoomManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    public void onGetFov(CallbackInfoReturnable<Double> ci) {

        ZoomManager zoomManager = ZoomManager.get();
        zoomManager.increaseZoom();
        zoomManager.decreaseZoom();
        zoomManager.manageSmoothCamera();

        if (zoomManager.isIncreasingZoom() || zoomManager.isDecreasingZoom()){
            zoomManager.printFeedback();
        }

        if (zoomManager.isZooming()) {
            ci.setReturnValue(zoomManager.getZoomLevel());
        }
    }
}