package net.zhuruoling.omms.controller.architectury.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import net.zhuruoling.omms.controller.architectury.network.*;
import net.zhuruoling.omms.controller.architectury.util.Util;

import java.util.Objects;

public class EventHandlers {
    public static UdpReceiver createChatReceiver(MinecraftServer server){
        return new UdpReceiver(server, Util.TARGET_CHAT, (s, m) -> {
            if (!ConstantStorage.getConfig().isEnableChatBridge())return;
            var broadcast = new Gson().fromJson(m, Broadcast.class);
            //logger.info(String.format("%s <%s[%s]> %s", Objects.requireNonNull(broadcast).getChannel(), broadcast.getPlayer(), broadcast.getServer(), broadcast.getContent()));
            if (broadcast.getPlayer().startsWith("\ufff3\ufff4")) {
                server.execute(() -> server.getPlayerManager().broadcast(Util.fromBroadcastToQQ(broadcast), false));

            }
            if (!Objects.equals(broadcast.getServer(), ConstantStorage.getConfig().getControllerName())) {
                server.execute(() -> server.getPlayerManager().broadcast(Util.fromBroadcast(broadcast), false));
            }
        });
    }

    public static UdpReceiver createInstructionReceiver(MinecraftServer server){
        return new UdpReceiver(server, Util.TARGET_CONTROL, (s, m) -> {

            Gson gson = new GsonBuilder().serializeNulls().create();
             Instruction instruction = gson.fromJson(m, Instruction.class);

            if (instruction.getControllerType() == ControllerTypes.FABRIC) {
                if (instruction.getType() == InstructionType.UPLOAD_STATUS) {
                   ConstantStorage.logger.info("Sending status.");
                    UdpBroadcastSender.Target target = gson.fromJson(instruction.getCommandString(), UdpBroadcastSender.Target.class);
                    Util.sendStatus(s,target);
                } else {
                    if (instruction.getType() == InstructionType.RUN_COMMAND) {
                        if (Objects.equals(instruction.getTargetControllerName(), ConstantStorage.getConfig().getControllerName())) {
                            ConstantStorage.logger.info("Received Command: %s".formatted(instruction.getCommandString()));
                            server.execute(() -> {
                                var dispatcher = server.getCommandManager().getDispatcher();
                                var results = dispatcher.parse(instruction.getCommandString(), server.getCommandSource());
                                server.getCommandManager().execute(results, instruction.getCommandString());
                            });

                        }
                    }
                }
            }
        });
    }
}
