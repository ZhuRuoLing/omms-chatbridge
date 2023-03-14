package net.zhuruoling.omms.chatbridge.architectury.config;

import com.mojang.logging.LogUtils;
import net.zhuruoling.omms.chatbridge.architectury.network.UdpReceiver;
import net.zhuruoling.omms.chatbridge.architectury.network.UdpBroadcastSender;
import org.slf4j.Logger;

public class ConstantStorage {
    public static Logger logger = LogUtils.getLogger();
    private static UdpReceiver chatReceiver;
    private static UdpBroadcastSender sender;

    public static UdpReceiver getChatReceiver() {
        return chatReceiver;
    }

    public static void setChatReceiver(UdpReceiver chatReceiver) {
        ConstantStorage.chatReceiver = chatReceiver;
    }

    public static UdpBroadcastSender getSender() {
        return sender;
    }

    public static void setSender(UdpBroadcastSender sender) {
        ConstantStorage.sender = sender;
    }


}
