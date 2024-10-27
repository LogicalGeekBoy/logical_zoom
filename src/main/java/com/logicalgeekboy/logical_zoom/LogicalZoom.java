package com.logicalgeekboy.logical_zoom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import org.lwjgl.glfw.GLFW;

public class LogicalZoom implements ClientModInitializer {

    private static boolean currentlyZoomed;
    private static KeyBinding keyBinding;
    private static boolean originalSmoothCameraEnabled;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static final float zoomLevel = (float) 0.23;

    @Override
    public void onInitializeClient() {
        keyBinding = new KeyBinding("key.logical_zoom.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.logical_zoom.zoom");

        currentlyZoomed = false;
        originalSmoothCameraEnabled = false;

        KeyBindingHelper.registerKeyBinding(keyBinding);
    }

    public static boolean isZooming() {
        return keyBinding.isPressed();
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
        return mc.options.smoothCameraEnabled;
    }

    private static void enableSmoothCamera() {
        mc.options.smoothCameraEnabled = true;
    }

    private static void disableSmoothCamera() {
        mc.options.smoothCameraEnabled = false;
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
