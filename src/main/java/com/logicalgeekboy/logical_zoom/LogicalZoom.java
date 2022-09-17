package com.logicalgeekboy.logical_zoom;

import com.logicalgeekboy.logical_zoom.config.ConfigHandler;
import com.logicalgeekboy.logical_zoom.config.ConfigUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * This class puts the "Logic" in "Logical Zoom" (i.e. it contains most part of
 * the mod logic). It controls the zoom activation and animation, smooth camera
 * management, and also registers the key bind such that it is configurable via
 * Minecraft's native option menu.
 * 
 * @author LogicalGeekBoy, marcelbpunkt
 *
 */
public class LogicalZoom implements ClientModInitializer {

	private static long lastZoomKeyActionTimestamp;
	private static KeyBinding zoomKeyBinding;
	private static boolean originalSmoothCameraEnabled;
	private static ZoomState currentState;

	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private static final ConfigHandler HANDLER = ConfigHandler.getInstance();

	@Override
	public void onInitializeClient() {
		/*
		 * The order of "key"/"category", the namespace and "zoom" is slightly different
		 * than in ConfigUtil in order to be consistent with the rest of the keybinds.
		 * It's also not included in the config classes because the zoom key is only
		 * configurable via Options -> Controls -> Key Binds.
		 */
		zoomKeyBinding = new KeyBinding("key." + ConfigUtil.NAMESPACE + ".zoom", InputUtil.Type.KEYSYM,
				InputUtil.GLFW_KEY_C, "category." + ConfigUtil.NAMESPACE + ".zoom");

		lastZoomKeyActionTimestamp = 0L;
		originalSmoothCameraEnabled = false;
		currentState = ZoomState.NO_ZOOM;

		KeyBindingHelper.registerKeyBinding(zoomKeyBinding);
	}

	/**
	 * Returns the current FOV multiplier, i.e. the inverse value of the current
	 * zoom factor.
	 * 
	 * @return the inverse value of the current zoom factor; if smooth zoom is
	 *         disabled, it returns either 1 or the inverse of the full zoom factor
	 *         depending on whether the zoom key is currently pressed. If smooth
	 *         zoom is enabled, it can also return values in between for when the
	 *         camera is currently zooming in or out.
	 */
	public static double getCurrentFOVMultiplier() {
		updateZoomStateAndSmoothCamera();
		double currentDurationMillis = getCurrentDuration();
		return currentState.getZoomFactorFunction().apply(1 / HANDLER.getZoomFactor(),
				HANDLER.getSmoothZoomDurationMillis(), currentDurationMillis);
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
				// zoom key is pressed while not currently zooming at all
				// => begin zoom-in if smooth zoom is enabled
				initZoomIn(0L);
				break;
			case ZOOM_OUT:
				// zoom key is pressed while zooming out
				// -> zoom back in from current camera position
				// (i.e. not from "fully zoomed out" position)
				initZoomIn(getCurrentRemainingDuration());
				break;
			case ZOOM_IN:
				// zoom key is still pressed while currently zooming in
				// -> continue zooming in until full zoom is reached
				if (hasMaxDurationPassed()) {
					currentState = ZoomState.FULL_ZOOM;
				}
				break;
			case FULL_ZOOM:
				// do nothing, i.e. keep up full zoom and full zoom state
			}
		} else {
			switch (currentState) {
			case FULL_ZOOM:
				// zoom key is released while fully zoomed in
				// -> begin zoom-out if smooth zoom is enabled
				initZoomOut(0L);
				break;
			case ZOOM_IN:
				// zoom key is released while zooming in
				// -> zoom back out from current camera position
				// (i.e. not from "fully zoomed in" position)
				initZoomOut(getCurrentRemainingDuration());
				break;
			case ZOOM_OUT:
				// zoom key is still released while currently zooming out
				// -> continue zooming back out to no zoom
				if (hasMaxDurationPassed()) {
					currentState = ZoomState.NO_ZOOM;
				}
				break;
			case NO_ZOOM:
				// do nothing, i.e. keep up 1.0x zoom and no zoom state
			}
		}
	}

	/**
	 * Changes the current state to {@link ZoomState#ZOOM_IN} or
	 * {@link ZoomState#FULL_ZOOM} depending on whether smooth zoom is enabled,
	 * determines the current camera position, remembers the player's smooth camera
	 * setting, and enables smooth camera while the zoom key is pressed.
	 * 
	 * @param offset zero if the camera is currently fully zoomed out,<br>
	 *               a value between 0 and the smooth zoom duration otherwise
	 */
	private static void initZoomIn(long offset) {
		markKeyEvent(offset);
		originalSmoothCameraEnabled = isSmoothCameraEnabled();
		enableSmoothCamera();
		currentState = HANDLER.isSmoothZoomEnabled() ? ZoomState.ZOOM_IN : ZoomState.FULL_ZOOM;
	}

	/**
	 * Changes the current state to {@link ZoomState#ZOOM_OUT} or
	 * {@link ZoomState#NO_ZOOM} depending on whether smooth zoom is enabled,
	 * determines the current camera position, and resets the smooth camera state to
	 * the player's original setting.
	 * 
	 * @param offset zero if the camera is currently fully zoomed in,<br>
	 *               a value between 0 and the smooth zoom duration otherwise
	 */
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

	/**
	 * Contains all zoom states and their respective {@link FOVMultiplier}.
	 * 
	 * @author marcelbpunkt
	 *
	 */
	private static enum ZoomState {

		/**
		 * No zoom. The zoom factor function always returns 1.
		 */
		NO_ZOOM((zf, md, x) -> 1.0),
		/**
		 * Zooming in. The zoom factor function returns a logarithmically increasing
		 * value between 1.0 and the full zoom factor.
		 */
		ZOOM_IN((zf, md, x) -> 1 - logAdjusted(zf, md, x)),
		/**
		 * Full zoom. The zoom factor function always returns the full zoom factor.
		 */
		FULL_ZOOM((zf, md, x) -> zf),
		/**
		 * Zooming out. The zoom factor function returns a logarithmically decreasing
		 * value between the full zoom factor and 1.0.
		 */
		ZOOM_OUT((zf, md, x) -> zf + logAdjusted(zf, md, x));

		////////////////////////////////
		// Function domain definition //
		////////////////////////////////

		/*
		 * The function domain influences the slope of the zoom factor function,
		 * especially near x_min and x_max. The lower y_min is, the steeper the slope is
		 * near x_min, zooming in/out faster at the beginning of the animation. The
		 * higher y_max is, the more shallow the slope is near x_max, zooming in/out
		 * more slowly towards the end of the animation.
		 */
		private static final double Y_MIN = -2.5;
		private static final double Y_MAX = 3.0;
		private static final double Y_RANGE = Y_MAX - Y_MIN;

		/*
		 * The min and max x values equal e^y_min and e^y_max respectively such that the
		 * x range matches the y range exactly (since e.g. log(e^y_min) == y_min)
		 */
		private static final double X_MIN = Math.pow(Math.E, Y_MIN);
		private static final double X_MAX = Math.pow(Math.E, Y_MAX);
		private static final double X_RANGE = X_MAX - X_MIN;

		private final FOVMultiplier zoomFactorFunction;

		private ZoomState(FOVMultiplier zoomFactorFunction) {
			this.zoomFactorFunction = zoomFactorFunction;
		}

		/**
		 * Returns the zoom factor function of the current zoom state.
		 * 
		 * @return the zoom factor function of the current zoom state
		 */
		public FOVMultiplier getZoomFactorFunction() {
			return zoomFactorFunction;
		}

		/**
		 * A logarithmic function that calculates and returns a value between
		 * {@code 0.0} and {@code 1.0 - zoomFactorInverse}.
		 * 
		 * @param zoomFactorInverse the inverse value of the zoom factor setting
		 * @param maxDuration       the smooth zoom duration setting
		 * @param currentDuration   the current duration counting from {@code 0.0} to
		 *                          {@code maxDuration}
		 * @return a logarithmic value between {@code 0} and
		 *         {@code 1.0 - zoomFactorInverse}
		 */
		private static double logAdjusted(double zoomFactorInverse, double maxDuration, double currentDuration) {
			return (Math.log(toDomain(maxDuration, currentDuration)) - Y_MIN) / (Y_RANGE) * (1.0 - zoomFactorInverse);
		}

		/**
		 * Converts a number in the range between {@code 0} and {@code maxDuration} to a
		 * number in the range between {@link #X_MIN} and {@link #X_MAX} and returns it.
		 * 
		 * @param maxDuration     the smooth zoom duration setting
		 * @param currentDuration the current duration counting from {@code 0.0} to
		 *                        {@code maxDuration}
		 * @return the converted number (between {@code X_MIN} and {@code X_MAX}
		 */
		private static double toDomain(double maxDuration, double currentDuration) {
			return X_RANGE / maxDuration * currentDuration + X_MIN;
		}
	}

	/**
	 * A function that calculates the current Field of View (FOV) multiplier which
	 * represents the inverse value of the current zoom factor. The current FOV
	 * multiplier function value depends on how much time has passed since the last
	 * zoom key press (zoom-in) or release (zoom-out) and must produce values
	 * between the inverse value of the zoom factor setting and 1.0.
	 * 
	 * @author marcelbpunkt
	 *
	 */
	@FunctionalInterface
	private static interface FOVMultiplier {

		double apply(double zoomFactor, double maxDuration, double currentDuration);
	}
}
