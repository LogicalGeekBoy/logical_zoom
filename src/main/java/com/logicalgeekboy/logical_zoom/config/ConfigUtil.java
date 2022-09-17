package com.logicalgeekboy.logical_zoom.config;

import com.logicalgeekboy.logical_zoom.LogicalZoom;

/**
 * Contains constants for all translation keys and default values except for the
 * zoom key bind which is defined in {@link LogicalZoom}. All properties stored
 * in the config file are String values in order to make use of
 * {@code Properties.load} and {@code Properties.store}.
 * <p>
 * The default values can also be retrieved in their correct data type via the
 * respective getter methods.
 * 
 * @author marcelbpunkt
 *
 */
public class ConfigUtil {

	/**
	 * The namespace of this mod
	 */
	public static final String NAMESPACE = "logical_zoom";

	private static final String CATEGORY_PREFIX = NAMESPACE + ".category";
	private static final String OPTION_PREFIX = NAMESPACE + ".option";
	private static final String TOOLTIP_PREFIX = NAMESPACE + ".tooltip";
	private static final String ERROR_PREFIX = NAMESPACE + ".error";

	/**
	 * The title displayed when this mod is configured via the Mod Menu screen
	 */
	public static final String MENU_TITLE = NAMESPACE + ".menu_title";

	/**
	 * The "General" settings category
	 */
	public static final String CATEGORY_GENERAL = CATEGORY_PREFIX + ".general";

	/**
	 * The translation key for the zoom factor setting
	 */
	public static final String OPTION_ZOOM_FACTOR = OPTION_PREFIX + ".zoom_factor";
	/**
	 * The translation key for the "Enable Smooth Zoom" setting
	 */
	public static final String OPTION_ENABLE_SMOOTH_ZOOM = OPTION_PREFIX + ".enable_smooth_zoom";
	/**
	 * The translation key for the smooth zoom duration setting
	 */
	public static final String OPTION_SMOOTH_ZOOM_DURATION_MILLIS = OPTION_PREFIX + ".smooth_zoom_duration_millis";

	/**
	 * The default value for the zoom factor setting
	 */
	public static final String DEFAULT_ZOOM_FACTOR = "3.0";
	/**
	 * The smallest possible zoom factor setting
	 */
	public static final double MIN_ZOOM_FACTOR = 1.0;
	/**
	 * The greatest possible zoom factor setting
	 */
	public static final double MAX_ZOOM_FACTOR = 5.0;
	/**
	 * The default "Enable Smooth Zoom" setting
	 */
	public static final String DEFAULT_ENABLE_SMOOTH_ZOOM = "true";
	/**
	 * The default smooth zoom duration setting in milliseconds
	 */
	public static final String DEFAULT_SMOOTH_ZOOM_DURATION_MILLIS = "120";
	/**
	 * The smallest possible smooth zoom duration setting.
	 * <p>
	 * The smooth zoom duration value must not be zero or it will lead to a division
	 * by zero!
	 */
	public static final long MIN_SMOOTH_ZOOM_DURATION_MILLIS = 1L;
	/**
	 * The greatest possible smooth zoom duration setting
	 */
	public static final long MAX_SMOOTH_ZOOM_DURATION_MILLIS = 10000L;

	/**
	 * The translation key for the zoom factor tooltip
	 */
	public static final String TOOLTIP_ZOOM_FACTOR = TOOLTIP_PREFIX + ".zoom_factor";
	/**
	 * The translation key for the "Enable Smooth Zoom" tooltip
	 */
	public static final String TOOLTIP_ENABLE_SMOOTH_ZOOM = TOOLTIP_PREFIX + ".enable_smooth_zoom";
	/**
	 * The translation key for the smooth zoom duration tooltip
	 */
	public static final String TOOLTIP_SMOOTH_ZOOM_DURATION_MILLIS = TOOLTIP_PREFIX + ".smooth_zoom_duration_millis";

	/**
	 * The file name of the properties file to which all settings are written
	 */
	public static final String CONFIG_FILE_NAME = "config/logical_zoom.properties";
	/**
	 * The translation key for the "zoom factor too small" error message
	 */
	public static final String ERROR_ZOOM_FACTOR_TOO_SMALL = ERROR_PREFIX + ".zoom_factor_too_small";
	/**
	 * The translation key for the "zoom factor too large" error message
	 */
	public static final String ERROR_ZOOM_FACTOR_TOO_LARGE = ERROR_PREFIX + ".zoom_factor_too_large";
	/**
	 * The translation key for the "smooth zoom duration too small" error message
	 */
	public static final String ERROR_SMOOTH_ZOOM_DURATION_MILLIS_TOO_SMALL = ERROR_PREFIX
			+ ".smooth_zoom_duration_millis_too_small";
	/**
	 * The translation key for the "smooth zoom duration too large" error message
	 */
	public static final String ERROR_SMOOTH_ZOOM_DURATION_MILLIS_TOO_LARGE = ERROR_PREFIX
			+ ".smooth_zoom_duration_millis_too_large";

	/**
	 * Returns the default zoom factor setting.
	 * 
	 * @return the default zoom factor setting
	 */
	public static double getDefaultZoomFactor() {
		return Double.parseDouble(DEFAULT_ZOOM_FACTOR);
	}

	/**
	 * Returns the default "Enable Smooth Zoom" setting.
	 * 
	 * @return the default "Enable Smooth Zoom" setting
	 */
	public static boolean getDefaultEnableSmoothZoom() {
		return Boolean.parseBoolean(DEFAULT_ENABLE_SMOOTH_ZOOM);
	}

	/**
	 * Returns the default smooth zoom duration setting in milliseconds.
	 * 
	 * @return the default smooth zoom duration setting in milliseconds
	 */
	public static long getDefaultSmoothZoomDurationMillis() {
		return Long.parseLong(DEFAULT_SMOOTH_ZOOM_DURATION_MILLIS);
	}
}
