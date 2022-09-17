package com.logicalgeekboy.logical_zoom.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen implements ModMenuApi {

	private static final ConfigHandler HANDLER = ConfigHandler.getInstance();

	public static Screen createConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
				.setTitle(Text.translatable(ConfigUtil.MENU_TITLE)).setSavingRunnable(HANDLER::saveProperties);

		ConfigCategory general = builder.getOrCreateCategory(Text.translatable(ConfigUtil.CATEGORY_GENERAL));

		// add zoom factor field (double value)
		general.addEntry(builder.entryBuilder()
				.startDoubleField(Text.translatable(ConfigUtil.OPTION_ZOOM_FACTOR), HANDLER.getZoomFactor())
				.setDefaultValue(ConfigUtil.getDefaultZoomFactor()).setSaveConsumer(HANDLER::setZoomFactor)
				.setMin(ConfigUtil.MIN_ZOOM_FACTOR).setMax(ConfigUtil.MAX_ZOOM_FACTOR)
				.setTooltip(Text.translatable(ConfigUtil.TOOLTIP_ZOOM_FACTOR))
				.setErrorSupplier(HANDLER::getZoomFactorError).build());

		// add enable smooth zoom button
		general.addEntry(builder.entryBuilder()
				.startBooleanToggle(Text.translatable(ConfigUtil.OPTION_ENABLE_SMOOTH_ZOOM),
						HANDLER.isSmoothZoomEnabled())
				.setDefaultValue(ConfigUtil.getDefaultEnableSmoothZoom()).setSaveConsumer(HANDLER::setSmoothZoomEnabled)
				.setTooltip(Text.translatable(ConfigUtil.TOOLTIP_ENABLE_SMOOTH_ZOOM)).build());

		// add smooth zoom duration field (double value)
		general.addEntry(builder.entryBuilder()
				.startLongField(Text.translatable(ConfigUtil.OPTION_SMOOTH_ZOOM_DURATION_MILLIS),
						HANDLER.getSmoothZoomDurationMillis())
				.setDefaultValue(ConfigUtil.getDefaultSmoothZoomDurationMillis())
				.setSaveConsumer(HANDLER::setSmoothZoomDurationMillis)
				.setMin(ConfigUtil.MIN_SMOOTH_ZOOM_DURATION_MILLIS).setMax(ConfigUtil.MAX_SMOOTH_ZOOM_DURATION_MILLIS)
				.setTooltip(Text.translatable(ConfigUtil.TOOLTIP_SMOOTH_ZOOM_DURATION_MILLIS))
				.setErrorSupplier(HANDLER::getSmoothZoomDurationMillisError).build());

		return builder.build();
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ConfigScreen::createConfigScreen;
	}
}
