package net.zhuruoling.omms.chatbridge.architectury.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.zhuruoling.omms.chatbridge.architectury.config.ConstantStorage;
import net.zhuruoling.omms.chatbridge.architectury.network.Broadcast;
import net.zhuruoling.omms.chatbridge.architectury.network.BroadcastType;
import net.zhuruoling.omms.chatbridge.architectury.network.UdpBroadcastSender;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Util {
    public static final Text LEFT_BRACKET = Text.of("[");
    public static final Text RIGHT_BRACKET = Text.of("]");
    public static final Text SPACE = Text.of(" ");

    public static final UdpBroadcastSender.Target TARGET_CHAT = new UdpBroadcastSender.Target("224.114.51.4", 10010);
    public static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static String joinFilePaths(String... args) {
        StringBuilder result = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            result.append("/").append(args[i]);
        }
        return result.toString();
    }

    public static void sendChatBroadcast(String text, String playerName) {
        sendBroadcast(text, playerName, BroadcastType.CHAT);
    }

    public static String formatAdvancementToString(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            return String.format("%s : %s",
                    Objects.requireNonNull(advancement.getDisplay()).getTitle().getString(),
                    advancement.getDisplay().getDescription().getString()
            );
        } else return "";
    }

    public static void sendAdvancementBroadcast(Advancement advancement, String playerName) {
        sendBroadcast(formatAdvancementToString(advancement), playerName, BroadcastType.ADVANCEMENT);
    }

    public static void sendBroadcast(String text, String name, BroadcastType broadcastType) {
        //System.out.printf("%s %s %s%n", text, name, broadcastType.name());
        if (text.isEmpty()) return;
        Broadcast broadcast = new Broadcast(name, text, randomStringGen(16), broadcastType);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String data = gson.toJson(broadcast, Broadcast.class);
        ConstantStorage.getSender().addToQueue(Util.TARGET_CHAT, data);
    }

    //[QQ] [displayName]: content
    public static Text getTextFromBroadcast(Broadcast broadcast) {
        return Texts.join(List.of(
                Text.of("["),
                Text.of(broadcast.getBroadcastType().name()).copyContentOnly().setStyle(Style.EMPTY.withColor(Formatting.GREEN)),
                Text.of("] ["),
                Text.of(broadcast.getDisplayName()).copyContentOnly().setStyle(Style.EMPTY.withColor(Formatting.AQUA)),
                Text.of("]: "),
                Text.of(broadcast.getContent())
        ), Text.empty());
    }

    public static String randomStringGen(int len) {
        String ch = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < len; i++) {
            Random random = new Random(System.nanoTime());
            int num = random.nextInt(62);
            stringBuffer.append(ch.charAt(num));
        }
        return stringBuffer.toString();
    }

    public static Text fromBroadcast(Broadcast broadcast) {
        return Text.empty();
    }
}
