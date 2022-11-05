package net.zhuruoling.omms.controller.architectury.fabric;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.zhuruoling.omms.controller.architectury.OMMSController;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import net.zhuruoling.omms.controller.architectury.events.EventHandlers;
import net.zhuruoling.omms.controller.architectury.network.UdpBroadcastSender;
import net.zhuruoling.omms.controller.architectury.util.Util;
import org.slf4j.Logger;

public class OMMSControllerFabric implements DedicatedServerModInitializer {
    Logger logger = LogUtils.getLogger();
    @Override
    public void onInitializeServer() {
        OMMSController.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            Util.registerCommand(dispatcher);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (ConstantStorage.getConfig().isEnableChatBridge()){
                var chatReceiver = EventHandlers.createChatReceiver(server);

                chatReceiver.setDaemon(true);
                chatReceiver.start();
                ConstantStorage.setChatReceiver(chatReceiver);
            }
            var instructionReceiver = EventHandlers.createInstructionReceiver(server);
            var sender = new UdpBroadcastSender();
            sender.setDaemon(true);
            instructionReceiver.setDaemon(true);
            instructionReceiver.start();

            sender.start();

            ConstantStorage.setSender(sender);

            ConstantStorage.setInstructionReceiver(instructionReceiver);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ConstantStorage.getSender().setStopped(true);
            if (ConstantStorage.getConfig().isEnableChatBridge())ConstantStorage.getChatReceiver().interrupt();
            ConstantStorage.getInstructionReceiver().interrupt();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            ConstantStorage.getSender().setStopped(true);
            if (ConstantStorage.getConfig().isEnableChatBridge())ConstantStorage.getChatReceiver().interrupt();
            ConstantStorage.getInstructionReceiver().interrupt();
        });
        logger.info("Hello World!");
    }
}
