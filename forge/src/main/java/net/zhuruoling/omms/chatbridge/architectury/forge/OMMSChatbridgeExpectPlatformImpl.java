package net.zhuruoling.omms.chatbridge.architectury.forge;

import net.zhuruoling.omms.chatbridge.architectury.OMMSChatbridgeExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class OMMSChatbridgeExpectPlatformImpl {
    /**
     * This is our actual method to {@link OMMSChatbridgeExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
