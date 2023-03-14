package net.zhuruoling.omms.chatbridge.architectury.fabric;

import net.zhuruoling.omms.chatbridge.architectury.OMMSChatbridgeExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class OMMSChatbridgeExpectPlatformImpl {
    /**
     * This is our actual method to {@link OMMSChatbridgeExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
