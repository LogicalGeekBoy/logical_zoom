package com.logicalgeekboy.logical_zoom.config;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.InputUtil;

public class ConfigUtil {

	static final String NAMESPACE = "logical_zoom";

	static final String OPTION_PREFIX = NAMESPACE + ".option";
	static final String TOOLTIP_PREFIX = NAMESPACE + ".tooltip";
	static final String WARN_PREFIX = NAMESPACE + ".warn";
	static final String ERROR_PREFIX = NAMESPACE + ".error";

	static final String CATEGORY_GENERAL = NAMESPACE + ".category.general";
	static final String MENU_TITLE = NAMESPACE + ".menu.title";

	static final String OPTION_ZOOM_FACTOR = OPTION_PREFIX + ".zoom_factor";
	static final String OPTION_ZOOM_KEY = OPTION_PREFIX + ".zoom_key";
	static final String OPTION_ENABLE_SMOOTH_ZOOM = OPTION_PREFIX + ".toggle_smooth_zoom";

	static final String DEFAULT_ZOOM_FACTOR = "5.0";
	static final double MIN_ZOOM_FACTOR = 1.0;
	static final double MAX_ZOOM_FACTOR = 5.0;
	static final String DEFAULT_ZOOM_KEY = Integer.toString(GLFW.GLFW_KEY_C);
	static final String DEFAULT_ENABLE_SMOOTH_ZOOM = "true";

	static final String TOOLTIP_ZOOM_FACTOR = TOOLTIP_PREFIX + ".zoom_factor";
	static final String TOOLTIP_ZOOM_KEY = TOOLTIP_PREFIX + ".zoom_key";
	static final String TOOLTIP_ENABLE_SMOOTH_ZOOM = TOOLTIP_PREFIX + ".toggle_smooth_zoom";

	static final String CONFIG_FILE_NAME = "config/logical_zoom.properties";
	static final String ERROR_CONFIG_FILE_READ = ERROR_PREFIX + ".config_file_read";
	static final String ERROR_CONFIG_FILE_WRITE = ERROR_PREFIX + ".config_file_write";
	static final String ERROR_ZOOM_FACTOR_TOO_SMALL = ERROR_PREFIX + ".zoom_factor_too_small";
	static final String ERROR_ZOOM_FACTOR_TOO_LARGE = ERROR_PREFIX + ".zoom_factor_too_large";

	static InputUtil.Key getDefaultZoomKey() {
		return getKeyFromCode(DEFAULT_ZOOM_KEY);
	}

	static InputUtil.Key getKeyFromCode(String property) {
		return getKeyFromCode(Integer.parseInt(property));
	}

	static InputUtil.Key getKeyFromCode(int keyCode) {
		return InputUtil.Type.KEYSYM.createFromCode(keyCode);
	}

	static double getDefaultZoomFactor() {
		return Double.parseDouble(DEFAULT_ZOOM_FACTOR);
	}

	static boolean getDefaultEnableSmoothZoom() {
		return Boolean.parseBoolean(DEFAULT_ENABLE_SMOOTH_ZOOM);
	}
}
