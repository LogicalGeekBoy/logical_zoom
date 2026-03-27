package com.logicalgeekboy.logical_zoom;

import net.fabricmc.api.ClientModInitializer;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

import org.lwjgl.glfw.GLFW;

public class LogicalZoom implements ClientModInitializer {

    private static boolean currentlyZoomed;
    private static KeyMapping keyBinding;
    private static boolean originalSmoothCameraEnabled;
    private static final Minecraft  mc = Minecraft.getInstance();

    public static final float zoomLevel = (float) 0.23;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("logicalzoom", "logical_zoom")
        );
        keyBinding = new KeyMapping("key.logical_zoom.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, category);

        KeyMappingHelper.registerKeyMapping(keyBinding);

        currentlyZoomed = false;
        originalSmoothCameraEnabled = false;

    }

    public static boolean isZooming() {
        return keyBinding.isDown();
    }

    public static void manageSmoothCamera() {
        if (zoomStarting()) {
            zoomStarted();
            enableSmoothCamera();
        }

        if (zoomStopping()) {
            zoomStopped();
            resetSmoothCamera();
        }
    }

    private static boolean isSmoothCamera() {
        return mc.options.smoothCamera;
    }

    private static void enableSmoothCamera() {
        mc.options.smoothCamera = true;
    }

    private static void disableSmoothCamera() {
        mc.options.smoothCamera = false;
    }

    private static boolean zoomStarting() {
        return isZooming() && !currentlyZoomed;
    }

    private static boolean zoomStopping() {
        return !isZooming() && currentlyZoomed;
    }

    private static void zoomStarted() {
        originalSmoothCameraEnabled = isSmoothCamera();
        currentlyZoomed = true;
    }

    private static void zoomStopped() {
        currentlyZoomed = false;
    }

    private static void resetSmoothCamera() {
        if (originalSmoothCameraEnabled) {
            enableSmoothCamera();
        } else {
            disableSmoothCamera();
        }
    }
}
