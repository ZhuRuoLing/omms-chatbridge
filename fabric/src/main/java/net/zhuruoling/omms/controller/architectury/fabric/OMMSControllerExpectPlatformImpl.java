package net.zhuruoling.omms.controller.architectury.fabric;

import net.zhuruoling.omms.controller.architectury.OMMSControllerExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class OMMSControllerExpectPlatformImpl {
    /**
     * This is our actual method to {@link OMMSControllerExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
