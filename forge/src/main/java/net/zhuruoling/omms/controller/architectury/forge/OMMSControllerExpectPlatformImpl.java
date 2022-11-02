package net.zhuruoling.omms.controller.architectury.forge;

import net.zhuruoling.omms.controller.architectury.OMMSControllerExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class OMMSControllerExpectPlatformImpl {
    /**
     * This is our actual method to {@link OMMSControllerExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
