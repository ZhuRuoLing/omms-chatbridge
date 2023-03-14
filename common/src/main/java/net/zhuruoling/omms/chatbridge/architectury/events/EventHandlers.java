package net.zhuruoling.omms.chatbridge.architectury.events;

import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import net.zhuruoling.omms.chatbridge.architectury.network.Broadcast;
import net.zhuruoling.omms.chatbridge.architectury.network.BroadcastType;
import net.zhuruoling.omms.chatbridge.architectury.network.UdpReceiver;
import net.zhuruoling.omms.chatbridge.architectury.util.Util;

import java.util.Objects;

public class EventHandlers {
    public static String oldId = "";

    public static UdpReceiver createChatReceiver(MinecraftServer server) {
        return new UdpReceiver(server, Util.TARGET_CHAT, (s, m) -> {
            var broadcast = new Gson().fromJson(m, Broadcast.class);
            if (Objects.equals(broadcast.getId(), EventHandlers.oldId)) return;//duplicate id
            //if (Objects.equals(broadcast.getServer(), "Forge Minecraft Server"))return;//HARDCODED SERVER NAME
            if (broadcast.getBroadcastType() != BroadcastType.QQ)return;
            EventHandlers.oldId = broadcast.getId();
            server.execute(() -> server.getPlayerManager().broadcast(Util.getTextFromBroadcast(broadcast), false));
        });
    }
}
