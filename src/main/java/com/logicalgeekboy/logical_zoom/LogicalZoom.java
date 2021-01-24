package com.logicalgeekboy.logical_zoom;

import com.logicalgeekboy.logical_zoom.render.ZoomManager;
import net.fabricmc.api.ClientModInitializer;

public class LogicalZoom implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ZoomManager.get();
        System.out.println("Logical Zoom is loaded, enjoy!");
    }


}
