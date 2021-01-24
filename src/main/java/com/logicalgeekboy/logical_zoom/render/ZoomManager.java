package com.logicalgeekboy.logical_zoom.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ZoomManager {

    private static ZoomManager instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean currentlyZoomed;
    private KeyBinding zoom;
    private KeyBinding increaseZoom;
    private KeyBinding decreaseZoom;
    private boolean originalSmoothCameraEnabled;
    private double zoomLevel;

    private ZoomManager() {
        this.currentlyZoomed = false;
        originalSmoothCameraEnabled = false;
        this.zoomLevel = 19.0d;
        this.registerKeyBindings();
    }

    public static ZoomManager get() {
        if (instance == null) {
            instance = new ZoomManager();
        }
        return instance;
    }

    private void registerKeyBindings() {
        this.zoom = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.logical_zoom.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.logical_zoom.zoom"));
        this.increaseZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.logical_zoom.increase", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, "category.logical_zoom.zoom"));
        this.decreaseZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.logical_zoom.decrease", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.logical_zoom.zoom"));
    }

    public boolean isZooming() {
        return zoom.isPressed();
    }

    public void manageSmoothCamera() {
        if (isZooming() && !currentlyZoomed) {
            originalSmoothCameraEnabled = mc.options.smoothCameraEnabled;
            currentlyZoomed = true;
            mc.options.smoothCameraEnabled = true;
        } else if (!isZooming() && currentlyZoomed) {
            currentlyZoomed = false;
            this.zoomLevel = 19.0d;
            mc.options.smoothCameraEnabled = originalSmoothCameraEnabled;
        }
    }

    public double getZoomLevel() {
        return this.zoomLevel;
    }

    public void increaseZoom() {
        if (this.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) && this.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) && this.isIncreasingZoom()) {
            this.zoomLevel -= 0.125d;
        } else if (this.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) && this.isIncreasingZoom()) {
            this.zoomLevel -= 0.25d;
        } else if (this.isIncreasingZoom()) {
            this.zoomLevel -= 0.5d;
        }
    }

    public void decreaseZoom() {
        if (this.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) && this.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) && this.isDecreasingZoom()) {
            this.zoomLevel += 0.125d;
        } else if (this.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) && this.isDecreasingZoom()) {
            this.zoomLevel += 0.25d;
        } else if (this.isDecreasingZoom()) {
            this.zoomLevel += 0.5d;
        }
    }

    public boolean isIncreasingZoom() {
        return this.increaseZoom.isPressed() && this.isZooming() && !(this.zoomLevel <= 1);
    }

    public boolean isDecreasingZoom() {
        return this.decreaseZoom.isPressed() && this.isZooming() && this.zoomLevel < 50;
    }

    private boolean isKeyPressed(int glfwKey) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), glfwKey);
    }

    public void printFeedback(){
        if (this.mc.player != null) {
            this.mc.player.sendMessage(new LiteralText("Zoom level is now " + this.zoomLevel), true);
        }
    }

}
