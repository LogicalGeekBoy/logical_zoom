package com.logicalgeekboy.logical_zoom;

import com.logicalgeekboy.logical_zoom.render.ZoomManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;

@Environment(EnvType.CLIENT)
public class LogicalZoom implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ZoomManager.get();
        LogManager.getLogger("Logical Zoom").info("Logical zoom is now loaded, enjoy!");
    }


}
