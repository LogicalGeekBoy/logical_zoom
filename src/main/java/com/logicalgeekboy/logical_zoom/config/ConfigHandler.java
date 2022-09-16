package com.logicalgeekboy.logical_zoom.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;

import com.logicalgeekboy.logical_zoom.LogicalZoom;
import com.mojang.logging.LogUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.text.Text;

public class ConfigHandler {

	private static final File CONFIG_FILE = new File(ConfigUtil.CONFIG_FILE_NAME);
	private static final ConfigHandler INSTANCE = new ConfigHandler();
	private static final Logger LOG = LogUtils.getLogger();

	private final ClientPlayerEntity player;

	private Properties properties;

	@SuppressWarnings("resource") // Minecraft client is auto-closable but must not be closed by us.
	private ConfigHandler() {
		this.player = MinecraftClient.getInstance().player;
		this.properties = new Properties();
		loadProperties();
	}

	public static ConfigHandler getInstance() {
		return INSTANCE;
	}

	public double getZoomFactor() {
		String property = (String) this.properties.getOrDefault(ConfigUtil.OPTION_ZOOM_FACTOR,
				ConfigUtil.DEFAULT_ENABLE_SMOOTH_ZOOM);
		return Double.parseDouble(property);
	}

	public InputUtil.Key getZoomKey() {
		return ConfigUtil.getKeyFromCode(Integer.toString(getZoomKeyCode()));
	}

	public int getZoomKeyCode() {
		return Integer.parseInt(
				(String) this.properties.getOrDefault(ConfigUtil.OPTION_ZOOM_KEY, ConfigUtil.DEFAULT_ZOOM_KEY));
	}

	public boolean isSmoothZoomEnabled() {
		return "true".equals(this.properties.getProperty(ConfigUtil.OPTION_ENABLE_SMOOTH_ZOOM));
	}

	public long getSmoothZoomDurationMillis() {
		return Long.parseLong((String) this.properties.getOrDefault(ConfigUtil.OPTION_SMOOTH_ZOOM_DURATION_MILLIS,
				ConfigUtil.DEFAULT_SMOOTH_ZOOM_DURATION_MILLIS));
	}

	public void setZoomFactor(double zoomFactor) {
		this.properties.put(ConfigUtil.OPTION_ZOOM_FACTOR, Double.toString(zoomFactor));
	}

	public void setZoomKey(Key zoomKey) {
		this.properties.put(ConfigUtil.OPTION_ZOOM_KEY, Integer.toString(zoomKey.getCode()));
		LogicalZoom.updateZoomKeyBinding(zoomKey);
	}

	public void setSmoothZoomEnabled(boolean isSmoothZoomEnabled) {
		this.properties.put(ConfigUtil.OPTION_ENABLE_SMOOTH_ZOOM, Boolean.toString(isSmoothZoomEnabled));
	}

	public void setSmoothZoomDurationMillis(long millis) {
		this.properties.put(ConfigUtil.OPTION_SMOOTH_ZOOM_DURATION_MILLIS, Long.toString(millis));
	}

	public Optional<Text> getZoomFactorError(double zoomFactor) {
		if (zoomFactor < ConfigUtil.MIN_ZOOM_FACTOR) {
			return Optional.of(Text.translatable(ConfigUtil.ERROR_ZOOM_FACTOR_TOO_SMALL));
		} else if (zoomFactor > ConfigUtil.MAX_ZOOM_FACTOR) {
			return Optional.of(Text.translatable(ConfigUtil.ERROR_ZOOM_FACTOR_TOO_LARGE));
		}

		return Optional.empty();
	}

	public Optional<Text> getSmoothZoomDurationMillisError(long millis) {
		if (millis < ConfigUtil.MIN_SMOOTH_ZOOM_DURATION_MILLIS) {
			return Optional.of(Text.translatable(ConfigUtil.ERROR_SMOOTH_ZOOM_DURATION_MILLIS_TOO_SMALL));
		} else if (millis > ConfigUtil.MAX_SMOOTH_ZOOM_DURATION_MILLIS) {
			return Optional.of(Text.translatable(ConfigUtil.ERROR_SMOOTH_ZOOM_DURATION_MILLIS_TOO_LARGE));
		}

		return Optional.empty();
	}

	@SuppressWarnings("resource") // Minecraft client is auto-closable but must not be closed by us.
	private void loadProperties() {
		if (!CONFIG_FILE.exists()) {
			loadDefaultProperties();
			return;
		}

		try (InputStream is = new FileInputStream(CONFIG_FILE)) {
			this.properties.load(is);
		} catch (IOException e) {
			LOG.error("Could not read from config file!", e);
			loadDefaultProperties();
		}

	}

	private void loadDefaultProperties() {
		properties.put(ConfigUtil.OPTION_ENABLE_SMOOTH_ZOOM, ConfigUtil.DEFAULT_ENABLE_SMOOTH_ZOOM);
		properties.put(ConfigUtil.OPTION_ZOOM_FACTOR, ConfigUtil.DEFAULT_ZOOM_FACTOR);
		properties.put(ConfigUtil.OPTION_ZOOM_KEY, ConfigUtil.DEFAULT_ZOOM_KEY);

		// since the default properties are only loaded if the properties file does not
		// exist or cannot be accessed for whatever reason, let's create/overwrite the
		// file.
		saveProperties();
	}

	@SuppressWarnings("resource") // Minecraft client is auto-closable but must not be closed by us.
	void saveProperties() {
		try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
			this.properties.store(out, null);
		} catch (IOException e) {
			LOG.error("Could not write to config file!", e);
		}
	}
}
