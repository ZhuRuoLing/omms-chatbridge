package net.zhuruoling.omms.chatbridge.architectury;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class OMMSChatbridge {
    private static final Logger logger = LogUtils.getLogger();
    public static final String MOD_ID = "ommschatbridge";
    public static void init() {
        String s = OMMSChatbridgeExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString();
        logger.info("OMMS ChatBridge Loaded!");
        logger.info("Hello World!");
    }
}
