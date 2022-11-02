package net.zhuruoling.omms.controller.architectury.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.zhuruoling.omms.controller.architectury.OMMSController;

public class OMMSControllerFabric implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        OMMSController.init();
    }
}
