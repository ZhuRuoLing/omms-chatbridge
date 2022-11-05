package net.zhuruoling.omms.controller.architectury.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.zhuruoling.omms.controller.architectury.announcement.Announcement;
import net.zhuruoling.omms.controller.architectury.command.Commands;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import net.zhuruoling.omms.controller.architectury.network.Broadcast;
import net.zhuruoling.omms.controller.architectury.network.ControllerTypes;
import net.zhuruoling.omms.controller.architectury.network.Status;
import net.zhuruoling.omms.controller.architectury.network.UdpBroadcastSender;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.*;

public class Util {
    public static final Text LEFT_BRACKET = Text.of("[");
    public static final Text RIGHT_BRACKET = Text.of("]");
    public static final Text SPACE = Text.of(" ");

    public static final UdpBroadcastSender.Target TARGET_CHAT = new UdpBroadcastSender.Target("224.114.51.4", 10086);
    public static final UdpBroadcastSender.Target TARGET_CONTROL = new UdpBroadcastSender.Target("224.114.51.4", 10087);

    public static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static String invokeHttpGetRequest(String httpUrl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(500);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    String temp;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            } else {
                connection.disconnect();
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result.toString();
    }


    public static int calculateTokenByDate(int password) {
        Date date = new Date();
        int i = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(date));
        int j = Integer.parseInt(new SimpleDateFormat("hhmm").format(date));
        int k = new SimpleDateFormat("yyyyMMddhhmm").format(date).hashCode();
        return calculateToken(password, i, j, k);
    }

    public static boolean resloveTokenByDate(int token, int password) {
        Date date = new Date();
        int i = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(date));
        int j = Integer.parseInt(new SimpleDateFormat("hhmm").format(date));
        int k = new SimpleDateFormat("yyyyMMddhhmm").format(date).hashCode();
        return resloveToken(token, password, i, j, k);
    }


    public static int calculateToken(int password, int i, int j, int k) {
        int token = 114514;
        token += i;
        token += (j - k);
        token = password ^ token;
        return token;
    }

    public static boolean resloveToken(int token, int password, int i, int j, int k) {
        int t = token;
        int var1 = t ^ password;

        var1 = var1 - i - (j - k);
        return var1 == 114514;
    }

    public static Text fromServerString(String displayName, String proxyName, boolean isCurrentServer, boolean isMissingServer) {
        Style style = Style.EMPTY;
        if (isMissingServer) {
            style = style.withColor(TextColor.fromFormatting(Formatting.RED));
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Missing server name mapping key.")));
        } else {
            if (isCurrentServer) {
                style = style.withColor(TextColor.fromFormatting(Formatting.YELLOW));
                style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Current server")));
            } else {
                style = style.withColor(Formatting.AQUA);
                style = style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server %s".formatted(proxyName)));
                style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Goto server %s".formatted(displayName))));
            }
        }
        Text name = Text.of(displayName).copyContentOnly().setStyle(style);
        List<Text> texts = List.of(Util.LEFT_BRACKET, name, Util.RIGHT_BRACKET);
        return Texts.join(texts, Text.empty());
    }

    public static Text fromAnnouncement(Announcement announcement) {
        Style style = Style.EMPTY;
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.FULL, FormatStyle.FULL, Chronology.ofLocale(Locale.getDefault()), Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Text timeText = Text.of("Published at %s\n".formatted(format.format(new Date(announcement.getTimeMillis()))))
                .copyContentOnly().setStyle(style.withColor(TextColor.fromFormatting(Formatting.LIGHT_PURPLE)).withBold(true));
        StringBuilder builder = new StringBuilder();
        for (String s : announcement.getContent()) {
            builder.append(s);
            builder.append("\n");
        }
        Text contentText = Text.of(builder.toString()).copyContentOnly();
        Text titleText = Text.of(announcement.getTitle() + "\n").copyContentOnly().setStyle(style.withColor(Formatting.GREEN).withBold(true));
        return Texts.join(List.of(titleText, timeText, contentText), Text.empty());
    }


    public static Text fromBroadcast(Broadcast broadcast) {
        Style style = Style.EMPTY;

        List<Text> texts = List.of(Text.of(broadcast.getChannel()).copyContentOnly().setStyle(style.withColor(Formatting.AQUA)),
                Text.of("<").copyContentOnly(),
                Text.of(broadcast.getPlayer()).copyContentOnly().setStyle(style.withColor(Formatting.YELLOW).withBold(true).withObfuscated(Objects.equals(broadcast.getServer(), "OMMS CENTRAL"))),
                LEFT_BRACKET.copyContentOnly(),
                Text.of(broadcast.getServer()).copyContentOnly().setStyle(style.withColor(Formatting.GREEN)),
                Text.of("]>").copyContentOnly(),
                Text.of(broadcast.getContent()).copyContentOnly()
        );
        return Texts.join(texts, Text.of(""));
    }

    public static Text fromBroadcastToQQ(Broadcast broadcast) {
        Style style = Style.EMPTY;

        List<Text> texts = List.of(
                Text.of(broadcast.getChannel()).copyContentOnly().setStyle(style.withColor(Formatting.AQUA)),
                Text.of("<").copyContentOnly(),
                Text.of(broadcast.getPlayer().replaceFirst("\ufff3\ufff4", "")).copyContentOnly().setStyle(style.withColor(Formatting.YELLOW).withBold(true).withObfuscated(Objects.equals(broadcast.getServer(), "OMMS CENTRAL"))),
                LEFT_BRACKET.copyContentOnly(),
                Text.of(broadcast.getServer() + " -> QQ").copyContentOnly().setStyle(style.withColor(Formatting.GREEN)),
                Text.of("]>").copyContentOnly(),
                Text.of(broadcast.getContent()).copyContentOnly()
        );
        return Texts.join(texts, Text.of(""));
    }

    public static String joinFilePaths(String... args) {
        StringBuilder result = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            result.append("/").append(args[i]);
        }
        return result.toString();
    }


    public static void sendChatBroadcast(String text, String playerName) {
        Broadcast broadcast = new Broadcast(ConstantStorage.getConfig().getChatChannel(),ConstantStorage.getConfig().getControllerName(),playerName, text);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String data = gson.toJson(broadcast, Broadcast.class);
        ConstantStorage.getSender().addToQueue(Util.TARGET_CHAT, data);
    }

    public static void sendStatus(MinecraftServer server, UdpBroadcastSender.Target target) {
        var status = new Status(
                ConstantStorage.getConfig().getControllerName(),
                ControllerTypes.FABRIC,
                server.getCurrentPlayerCount(),
                server.getMaxPlayerCount(),
                Arrays.asList(server.getPlayerNames())
        );
        ConstantStorage.getSender().createMulticastSocketCache(target);
        try {
            Thread.sleep(50);
            ConstantStorage.getSender().addToQueue(target, gson.toJson(status));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getAnnouncementToPlayerFromUrl(CommandContext<ServerCommandSource> context, String url) {
        String result = Util.invokeHttpGetRequest(url);
        if (result != null) {
            if (result.equals("\"NO_ANNOUNCEMENT\"")) {
                Text text = Texts.join(Text.of("No announcement.").copyContentOnly().getWithStyle(Style.EMPTY.withColor(Formatting.AQUA)), Text.empty());
                context.getSource().sendFeedback(text, false);
                return 0;
            }
            System.out.println(result);
            try {
                String jsonStr = new String(Base64.getDecoder().decode(result.substring(1, result.length() - 2)), StandardCharsets.UTF_8);
                Gson gson = new GsonBuilder().serializeNulls().create();
                var announcement = gson.fromJson(jsonStr, Announcement.class);
                context.getSource().sendFeedback(Util.fromAnnouncement(announcement), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Text text = Texts.join(Text.of("No announcement.").copyContentOnly().getWithStyle(Style.EMPTY.withColor(Formatting.AQUA)), Text.empty());
            context.getSource().sendFeedback(text, false);
            return 0;
        }

        return 0;
    }


    public static void registerCommand(MinecraftServer minecraftServer) {
        var dispatcher = minecraftServer.getCommandManager().getDispatcher();
        registerCommand(dispatcher);
    }

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        ConstantStorage.logger.info("Registering Commands!");
        dispatcher.register(Commands.ANNOUNCEMENT_COMMAND);
        dispatcher.register(Commands.MENU_COMMAND);
        dispatcher.register(Commands.SENDTOCONSOLE_COMMAND);
        dispatcher.register(Commands.QQ_CHAT_SEND);
    }
}
