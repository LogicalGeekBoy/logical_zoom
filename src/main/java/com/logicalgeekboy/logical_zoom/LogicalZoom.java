package com.logicalgeekboy.logical_zoom;

import com.logicalgeekboy.logical_zoom.config.ConfigHandler;
import com.logicalgeekboy.logical_zoom.config.ConfigUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class LogicalZoom implements ClientModInitializer {

	private static long lastZoomKeyActionTimestamp;
	private static KeyBinding zoomKeyBinding;
	private static boolean originalSmoothCameraEnabled;
	private static ZoomState currentState;

	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private static final ConfigHandler HANDLER = ConfigHandler.getInstance();

	@Override
	public void onInitializeClient() {
		// TODO add Mod Menu config for smooth zoom on/off + duration (#2)
		zoomKeyBinding = new KeyBinding(ConfigUtil.OPTION_ZOOM_KEY, InputUtil.Type.KEYSYM, HANDLER.getZoomKeyCode(),
				ConfigUtil.CATEGORY_ZOOM_KEY);

		lastZoomKeyActionTimestamp = 0L;
		originalSmoothCameraEnabled = false;
		currentState = ZoomState.NO_ZOOM;

		KeyBindingHelper.registerKeyBinding(zoomKeyBinding);
	}

	public static double getCurrentZoomLevel() {
		updateZoomStateAndSmoothCamera();
		double currentDurationMillis = getCurrentDuration();
		return currentState.getZoomFactorFunction().apply(1 / HANDLER.getZoomFactor(),
				HANDLER.getSmoothZoomDurationMillis(), currentDurationMillis);
	}

	public static void updateZoomKeyBinding(InputUtil.Key zoomKey) {
		// TODO can we make this a thing solely managed by Mod Menu somehow? This seems
		// a bit hacky.
		zoomKeyBinding.setBoundKey(zoomKey);
		KeyBinding.updateKeysByCode();
	}

	private static boolean isZoomKeyPressed() {
		return zoomKeyBinding.isPressed();
	}

	private static boolean hasMaxDurationPassed() {
		return getCurrentRemainingDuration() <= 0.0;
	}

	private static void updateZoomStateAndSmoothCamera() {
		if (isZoomKeyPressed()) {
			switch (currentState) {
			case NO_ZOOM:
				initZoomIn(0L);
				break;
			case ZOOM_OUT:
				initZoomIn(getCurrentRemainingDuration());
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
			case FULL_ZOOM:
				initZoomOut(0L);
				break;
			case ZOOM_IN:
				initZoomOut(getCurrentRemainingDuration());
				break;
			case ZOOM_OUT:
				if (hasMaxDurationPassed()) {
					currentState = ZoomState.NO_ZOOM;
				}
				break;
			case NO_ZOOM:
				// do nothing
			}
		}
	}

	private static void initZoomIn(long offset) {
		markKeyEvent(offset);
		originalSmoothCameraEnabled = isSmoothCameraEnabled();
		enableSmoothCamera();
		currentState = HANDLER.isSmoothZoomEnabled() ? ZoomState.ZOOM_IN : ZoomState.FULL_ZOOM;
	}

	private static void initZoomOut(long offset) {
		markKeyEvent(offset);
		resetSmoothCamera();
		currentState = HANDLER.isSmoothZoomEnabled() ? ZoomState.ZOOM_OUT : ZoomState.NO_ZOOM;
	}

	private static void markKeyEvent(long offset) {
		lastZoomKeyActionTimestamp = System.currentTimeMillis() - offset;
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

	private static long getCurrentDuration() {
		return System.currentTimeMillis() - lastZoomKeyActionTimestamp;
	}

	private static long getCurrentRemainingDuration() {
		return HANDLER.getSmoothZoomDurationMillis() - getCurrentDuration();
	}

	private static enum ZoomState {

		/**
		 * No zoom. The zoom factor function always returns 1.
		 */
		NO_ZOOM((zf, md, x) -> 1.0),
		/**
		 * Zooming in. The zoom factor function returns a value
		 */
		ZOOM_IN((zf, md, x) -> 1 - logAdjusted(zf, md, x)), FULL_ZOOM((zf, md, x) -> zf),
		ZOOM_OUT((zf, md, x) -> zf + logAdjusted(zf, md, x));

		// The y range influences the slope of the zoom factor function especially near
		// x_min and x_max.
		// The lower y_min is, the steeper the slope is near x_min.
		// The higher y_max is, the more shallow the slope is near x_max.
		private static final double Y_MIN = -3.0;
		private static final double Y_MAX = 3.0;
		private static final double Y_RANGE = Y_MAX - Y_MIN;
		// the min and max x values equal e^y_min and e^y_max respectively because we
		// want the logarithmic function to produce output values between 0 and 1.
		private static final double X_MIN = Math.pow(Math.E, Y_MIN);
		private static final double X_MAX = Math.pow(Math.E, Y_MAX);
		private static final double X_RANGE = X_MAX - X_MIN;

		private final ZoomFactorFunction zoomFactorFunction;

		private ZoomState(ZoomFactorFunction zoomFactorFunction) {
			this.zoomFactorFunction = zoomFactorFunction;
		}

		public ZoomFactorFunction getZoomFactorFunction() {
			return zoomFactorFunction;
		}

		private static double logAdjusted(double zoomFactor, double maxDuration, double currentDuration) {
			return (Math.log(toDomain(maxDuration, currentDuration)) - Y_MIN) / (Y_RANGE) * (1.0 - zoomFactor);
		}

		private static double toDomain(double maxDuration, double currentDuration) {
			return X_RANGE / maxDuration * currentDuration + X_MIN;
		}
	}

	@FunctionalInterface
	private static interface ZoomFactorFunction {

		double apply(double zoomLevel, double maxDuration, double currentDuration);
	}
}
