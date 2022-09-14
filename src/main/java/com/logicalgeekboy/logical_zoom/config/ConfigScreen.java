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

		// add zoom key field
		general.addEntry(builder.entryBuilder()
				.startKeyCodeField(Text.translatable(ConfigUtil.OPTION_ZOOM_KEY), HANDLER.getZoomKey())
				.setDefaultValue(ConfigUtil.getDefaultZoomKey()).setKeySaveConsumer(HANDLER::setZoomKey)
				.setTooltip(Text.translatable(ConfigUtil.OPTION_ZOOM_KEY)).build());

		// add zoom factor field (double value)
		general.addEntry(builder.entryBuilder()
				.startDoubleField(Text.translatable(ConfigUtil.OPTION_ZOOM_FACTOR), HANDLER.getZoomFactor())
				.setDefaultValue(Double.parseDouble(ConfigUtil.DEFAULT_ZOOM_FACTOR))
				.setSaveConsumer(HANDLER::setZoomFactor).setMin(ConfigUtil.MIN_ZOOM_FACTOR)
				.setMax(ConfigUtil.MAX_ZOOM_FACTOR).setTooltip(Text.translatable(ConfigUtil.TOOLTIP_ZOOM_FACTOR))
				.setErrorSupplier(HANDLER::getZoomFactorError).build());

		general.addEntry(builder.entryBuilder()
				.startBooleanToggle(Text.translatable(ConfigUtil.OPTION_ENABLE_SMOOTH_ZOOM),
						HANDLER.isSmoothZoomEnabled())
				.setDefaultValue(ConfigUtil.getDefaultEnableSmoothZoom()).setSaveConsumer(HANDLER::setSmoothZoomEnabled)
				.build());

		return builder.build();
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ConfigScreen::createConfigScreen;
	}
}
