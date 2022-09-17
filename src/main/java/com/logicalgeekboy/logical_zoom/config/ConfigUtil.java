package com.logicalgeekboy.logical_zoom.config;

public class ConfigUtil {

	public static final String NAMESPACE = "logical_zoom";

	public static final String CATEGORY_PREFIX = NAMESPACE + ".category";
	public static final String OPTION_PREFIX = NAMESPACE + ".option";
	public static final String TOOLTIP_PREFIX = NAMESPACE + ".tooltip";
	public static final String ERROR_PREFIX = NAMESPACE + ".error";

	public static final String MENU_TITLE = NAMESPACE + ".menu_title";

	public static final String CATEGORY_GENERAL = CATEGORY_PREFIX + ".general";

	public static final String OPTION_ZOOM_FACTOR = OPTION_PREFIX + ".zoom_factor";
	public static final String OPTION_ENABLE_SMOOTH_ZOOM = OPTION_PREFIX + ".enable_smooth_zoom";
	public static final String OPTION_SMOOTH_ZOOM_DURATION_MILLIS = OPTION_PREFIX + ".smooth_zoom_duration_millis";

	public static final String DEFAULT_ZOOM_FACTOR = "3.0";
	public static final double MIN_ZOOM_FACTOR = 1.0;
	public static final double MAX_ZOOM_FACTOR = 5.0;
	public static final String DEFAULT_ENABLE_SMOOTH_ZOOM = "true";
	public static final String DEFAULT_SMOOTH_ZOOM_DURATION_MILLIS = "120";
	// The duration value must not be zero or it will lead to a division by zero!
	public static final long MIN_SMOOTH_ZOOM_DURATION_MILLIS = 1L;
	public static final long MAX_SMOOTH_ZOOM_DURATION_MILLIS = 10000L;

	public static final String TOOLTIP_ZOOM_FACTOR = TOOLTIP_PREFIX + ".zoom_factor";
	public static final String TOOLTIP_ENABLE_SMOOTH_ZOOM = TOOLTIP_PREFIX + ".enable_smooth_zoom";
	public static final String TOOLTIP_SMOOTH_ZOOM_DURATION_MILLIS = TOOLTIP_PREFIX + ".smooth_zoom_duration_millis";

	public static final String CONFIG_FILE_NAME = "config/logical_zoom.properties";
	public static final String ERROR_ZOOM_FACTOR_TOO_SMALL = ERROR_PREFIX + ".zoom_factor_too_small";
	public static final String ERROR_ZOOM_FACTOR_TOO_LARGE = ERROR_PREFIX + ".zoom_factor_too_large";
	public static final String ERROR_SMOOTH_ZOOM_DURATION_MILLIS_TOO_SMALL = ERROR_PREFIX
			+ ".smooth_zoom_duration_millis_too_small";
	public static final String ERROR_SMOOTH_ZOOM_DURATION_MILLIS_TOO_LARGE = ERROR_PREFIX
			+ ".smooth_zoom_duration_millis_too_large";

	public static double getDefaultZoomFactor() {
		return Double.parseDouble(DEFAULT_ZOOM_FACTOR);
	}

	public static boolean getDefaultEnableSmoothZoom() {
		return Boolean.parseBoolean(DEFAULT_ENABLE_SMOOTH_ZOOM);
	}

	public static long getDefaultSmoothZoomDurationMillis() {
		return Long.parseLong(DEFAULT_SMOOTH_ZOOM_DURATION_MILLIS);
	}
}
