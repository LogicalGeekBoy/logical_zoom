package com.logicalgeekboy.logical_zoom;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;

import net.minecraft.util.Identifier;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.MinecraftClient;

import org.lwjgl.glfw.GLFW;

public class LogicalZoom implements ClientModInitializer {

	private static Boolean currentlyZoomed;
	private static FabricKeyBinding keyBinding;
	private static Boolean originalSmoothCameraEnabled;
	
	@Override
	public void onInitializeClient() {
		keyBinding = FabricKeyBinding.Builder.create(new Identifier("logical_zoom", "zoom"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "Logical Zoom").build();
		currentlyZoomed = false;
		originalSmoothCameraEnabled = false;

		KeyBindingRegistry.INSTANCE.addCategory("Logical Zoom");
		KeyBindingRegistry.INSTANCE.register(keyBinding);
	}

	public static Boolean isZooming() {
		return keyBinding.isPressed();
	}

	public static double level() {
		return 19.0; // Hardcoded for now, same FoV amount as Optifine as far as I can make out
	}

	public static void manageSmoothCamera() {
		if(zoomStarting()) {
			zoomStarted();
            enableSmoothCamera();
        }
			
		if(zoomStopping()) {
			zoomStopped();
			resetSmoothCamera();
		}
	}

	private static Boolean isSmoothCamera() {
		return MinecraftClient.getInstance().options.smoothCameraEnabled;
	}

	private static void enableSmoothCamera() {
		MinecraftClient.getInstance().options.smoothCameraEnabled = true;
	}

	private static void disableSmoothCamera() {
		MinecraftClient.getInstance().options.smoothCameraEnabled = false;
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
		if(originalSmoothCameraEnabled) {
			enableSmoothCamera();
		} else {
			disableSmoothCamera();
		}
	}
}
