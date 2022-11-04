package net.zhuruoling.omms.controller.architectury.config;

import com.mojang.logging.LogUtils;
import net.zhuruoling.omms.controller.architectury.network.UdpBroadcastSender;
import net.zhuruoling.omms.controller.architectury.network.UdpReceiver;
import org.slf4j.Logger;

public class ConstantStorage {
    public static Logger logger = LogUtils.getLogger();
    private static UdpReceiver chatReceiver;
    private static UdpReceiver instructionReceiver;
    private static UdpBroadcastSender sender;
    private static Config config;

    public static UdpReceiver getChatReceiver() {
        return chatReceiver;
    }

    public static void setChatReceiver(UdpReceiver chatReceiver) {
        ConstantStorage.chatReceiver = chatReceiver;
    }

    public static UdpReceiver getInstructionReceiver() {
        return instructionReceiver;
    }

    public static void setInstructionReceiver(UdpReceiver instructionReceiver) {
        ConstantStorage.instructionReceiver = instructionReceiver;
    }

    public static UdpBroadcastSender getSender() {
        return sender;
    }

    public static void setSender(UdpBroadcastSender sender) {
        ConstantStorage.sender = sender;
    }

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        ConstantStorage.config = config;
    }
}
