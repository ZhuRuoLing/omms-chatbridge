package net.zhuruoling.omms.controller.architectury.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.zhuruoling.omms.controller.architectury.config.Config;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import net.zhuruoling.omms.controller.architectury.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.zhuruoling.omms.controller.architectury.util.Util.getAnnouncementToPlayerFromUrl;

public class Commands {
    public static LiteralArgumentBuilder<ServerCommandSource> QQ_CHAT_SEND = LiteralArgumentBuilder.<ServerCommandSource>literal("qq")
            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(0))
            .then(
                    RequiredArgumentBuilder.<ServerCommandSource, String>argument("content", StringArgumentType.greedyString()).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(0)).executes(context -> {
                                var content = StringArgumentType.getString(context, "content");
                                var sender = context.getSource().getDisplayName().getString();
                                Util.sendChatBroadcast(content, "\ufff3\ufff4" + sender);
                                return 0;
                            }
                    )
            );

    public static LiteralArgumentBuilder<ServerCommandSource> ANNOUNCEMENT_COMMAND = LiteralArgumentBuilder.<ServerCommandSource>literal("announcement")
            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("latest")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(0))
                    .executes(context -> {
                        String url = "http://%s:%d/announcement/latest".formatted(ConstantStorage.getConfig().getHttpQueryAddr(), ConstantStorage.getConfig().getHttpQueryPort());
                        return getAnnouncementToPlayerFromUrl(context, url);
                    })
            )
            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(0))
            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("get")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(0))
                    .then(
                            RequiredArgumentBuilder.<ServerCommandSource, String>argument("name", word())
                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(0))
                                    .executes(context -> {
                                        String name = StringArgumentType.getString(context, "name");
                                        String url = "http://%s:%d/announcement/get/%s".formatted(ConstantStorage.getConfig().getHttpQueryAddr(), ConstantStorage.getConfig().getHttpQueryPort(), name);
                                        return getAnnouncementToPlayerFromUrl(context, url);
                                    })
                    )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("list")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(0)).executes(context -> {
                        String url = "http://%s:%d/announcement/list".formatted(ConstantStorage.getConfig().getHttpQueryAddr(), ConstantStorage.getConfig().getHttpQueryPort());
                        String result = Objects.requireNonNull(Util.invokeHttpGetRequest(url));
                        try {
                            String[] list = new Gson().fromJson(result, String[].class);
                            ArrayList<Text> texts = new ArrayList<>();
                            for (String s : list) {
                                System.out.println(s);
                                var text = Texts.join(
                                                List.of(
                                                        Text.of("["),
                                                        Text.of(s)
                                                                .copyContentOnly()
                                                                .setStyle(
                                                                        Style.EMPTY
                                                                                .withColor(Formatting.GREEN)
                                                                ),
                                                        Text.of("]")
                                                ),
                                                Text.of("")
                                        )
                                        .copyContentOnly()
                                        .setStyle(
                                                Style.EMPTY
                                                        .withHoverEvent(
                                                                new HoverEvent(
                                                                        HoverEvent.Action.SHOW_TEXT,
                                                                        Text.of("Click to get announcement.")
                                                                )
                                                        )
                                                        .withClickEvent(
                                                                new ClickEvent(
                                                                        ClickEvent.Action.RUN_COMMAND,
                                                                        "/announcement get %s".formatted(s)
                                                                )
                                                        )
                                        );
                                System.out.println(text);

                            }
                            System.out.println(texts);
                            context.getSource().sendFeedback(Text.of("-------Announcements-------"), false);
                            context.getSource().sendFeedback(Text.of(""), false);
                            context.getSource().sendFeedback(Texts.join(texts, Text.of(" ")), false);
                            context.getSource().sendFeedback(Text.of(""), false);
                            return 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    })
            );

    public static LiteralArgumentBuilder<ServerCommandSource> SENDTOCONSOLE_COMMAND = LiteralArgumentBuilder.<ServerCommandSource>literal("sendToConsole")
            .then(
                    RequiredArgumentBuilder.<ServerCommandSource, String>argument("content", StringArgumentType.greedyString()).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)).executes(context -> {
                                ConstantStorage.logger.info("<OMMS_Controller> %s".formatted(StringArgumentType.getString(context, "content")));
                                return 0;
                            }

                    )
            );

    public static LiteralArgumentBuilder<ServerCommandSource> MENU_COMMAND = literal("menu")
            .then(argument("data", NbtCompoundArgumentType.nbtCompound()).requires(source -> source.hasPermissionLevel(0)).executes(context -> {
                try {
                    NbtCompound compound = NbtCompoundArgumentType.getNbtCompound(context, "data");
                    if (!compound.contains("servers")) {
                        context.getSource().sendError(Text.of("Wrong server data."));
                        return -1;
                    }
                    String data = compound.getString("servers");
                    String[] servers = new GsonBuilder().serializeNulls().create().fromJson(data, String[].class);
                    ArrayList<Text> serverEntries = new ArrayList<>();
                    String currentServer = ConstantStorage.getConfig().getWhitelistName();
                    for (String server : servers) {
                        boolean isCurrentServer = Objects.equals(currentServer, server);
                        Config.ServerMapping mapping = ConstantStorage.getConfig().getServerMappings().get(server);
                        if (Objects.isNull(mapping)) {
                            serverEntries.add(Util.fromServerString(server, null, false, true));
                            continue;
                        }
                        serverEntries.add(Util.fromServerString(mapping.getDisplayName(), mapping.getProxyName(), isCurrentServer, false));
                    }
                    Text serverText = Texts.join(serverEntries, Util.SPACE);
                    context.getSource().sendFeedback(Text.of("----------Welcome to %s server!----------".formatted(ConstantStorage.getConfig().getControllerName())), false);
                    context.getSource().sendFeedback(Text.of("    "), false);
                    context.getSource().sendFeedback(serverText, false);
                    context.getSource().sendFeedback(Text.of("Type \"/announcement latest\" to fetch latest announcement."), false);
                    return 1;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            }));
}
