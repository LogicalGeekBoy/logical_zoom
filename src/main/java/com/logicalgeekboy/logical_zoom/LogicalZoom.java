package com.logicalgeekboy.logical_zoom;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class LogicalZoom implements ClientModInitializer {

	private static long lastZoomKeyActionTimestamp;
	private static KeyBinding keyBinding;
	private static boolean originalSmoothCameraEnabled;
	private static ZoomState currentState;

	private static final MinecraftClient MC = MinecraftClient.getInstance();
	// The zoom level is a multiplier of the FOV value (in degrees) which means
	// that values < 1 decrease the FOV and thus increase the zoom!
	// TODO think about making configurable (#2)
	private static final double ZOOM_LEVEL = 0.23;
	// TODO make configurable (#2)
	// better to make it a long since it's compared with System.currentTimeMillis()
	private static final long SMOOTH_ZOOM_DURATION_MILLIS = 170;

	@Override
	public void onInitializeClient() {
		// TODO add Mod Menu config for smooth zoom on/off + duration (#2)
		keyBinding = new KeyBinding("key.logical_zoom.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C,
				"category.logical_zoom.zoom");

		lastZoomKeyActionTimestamp = 0L;
		originalSmoothCameraEnabled = false;
		currentState = ZoomState.NO_ZOOM;

		KeyBindingHelper.registerKeyBinding(keyBinding);
	}

	private static boolean isZoomKeyPressed() {
		return keyBinding.isPressed();
	}

	private static boolean hasMaxDurationPassed() {
		return System.currentTimeMillis() - lastZoomKeyActionTimestamp >= SMOOTH_ZOOM_DURATION_MILLIS;
	}

	public static double getCurrentZoomLevel() {
		updateZoomStateAndSmoothCamera();
		double currentDurationMillis = getCurrentDuration();
		return currentState.getZoomLevelFunction().apply(ZOOM_LEVEL, SMOOTH_ZOOM_DURATION_MILLIS,
				currentDurationMillis);
	}

	private static void updateZoomStateAndSmoothCamera() {
		if (isZoomKeyPressed()) {
			switch (currentState) {
			case NO_ZOOM:
				originalSmoothCameraEnabled = isSmoothCameraEnabled();
				enableSmoothCamera();
			case ZOOM_OUT:
				currentState = ZoomState.ZOOM_IN;
				markKeyAction();
				break;
			case ZOOM_IN:
				if (hasMaxDurationPassed()) {
					currentState = ZoomState.FULL_ZOOM;
				}
				break;
			case FULL_ZOOM:
				// do nothing
			}
		} else {
			switch (currentState) {
			case ZOOM_IN:
			case FULL_ZOOM:
				currentState = ZoomState.ZOOM_OUT;
				markKeyAction();
			case ZOOM_OUT:
				if (hasMaxDurationPassed()) {
					currentState = ZoomState.NO_ZOOM;
					resetSmoothCamera();
				}
				break;
			case NO_ZOOM:
				// do nothing
			}
		}
	}

	private static void markKeyAction() {
		lastZoomKeyActionTimestamp = System.currentTimeMillis();
	}

	private static boolean isSmoothCameraEnabled() {
		return MC.options.smoothCameraEnabled;
	}

	private static void enableSmoothCamera() {
		MC.options.smoothCameraEnabled = true;
	}

	private static void resetSmoothCamera() {
		MC.options.smoothCameraEnabled = originalSmoothCameraEnabled;
	}

	private static double getCurrentDuration() {
		return System.currentTimeMillis() - lastZoomKeyActionTimestamp;
	}

	private static enum ZoomState {

		NO_ZOOM((zl, md, x) -> 1.0), ZOOM_IN((zl, md, x) -> 1 - logAdjusted(zl, md, x)), FULL_ZOOM((zl, md, x) -> zl),
		ZOOM_OUT((zl, md, x) -> zl + logAdjusted(zl, md, x));

		private static final double Y_MIN = -3.0;
		private static final double Y_MAX = 3.0;
		private static final double Y_RANGE = Y_MAX - Y_MIN;
		private static final double X_MIN = Math.pow(Math.E, Y_MIN);
		private static final double X_MAX = Math.pow(Math.E, Y_MAX);
		private static final double X_RANGE = X_MAX - X_MIN;

		private final ZoomLevelFunction zoomLevelFunction;

		private ZoomState(ZoomLevelFunction zoomLevelFunction) {
			this.zoomLevelFunction = zoomLevelFunction;
		}

		public ZoomLevelFunction getZoomLevelFunction() {
			return zoomLevelFunction;
		}

		private static double logAdjusted(double zoomLevel, double maxDuration, double currentDuration) {
			return (Math.log(toDomain(maxDuration, currentDuration)) - Y_MIN) / (Y_RANGE) * (1.0 - zoomLevel);
		}

		private static double toDomain(double maxDuration, double currentDuration) {
			return X_RANGE / maxDuration * currentDuration + X_MIN;
		}
	}

	@FunctionalInterface
	private static interface ZoomLevelFunction {

		double apply(double zoomLevel, double maxDuration, double currentDuration);
	}
}
