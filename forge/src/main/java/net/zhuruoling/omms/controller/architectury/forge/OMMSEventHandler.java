package net.zhuruoling.omms.controller.architectury.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zhuruoling.omms.controller.architectury.OMMSController;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import net.zhuruoling.omms.controller.architectury.events.EventHandlers;
import net.zhuruoling.omms.controller.architectury.network.UdpBroadcastSender;
import net.zhuruoling.omms.controller.architectury.util.Util;

@Mod.EventBusSubscriber(modid = OMMSController.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.DEDICATED_SERVER)
public class OMMSEventHandler {
/*
@Mod.EventBusSubscriber(modid = "mymod", bus = Bus.FORGE, value = Dist.CLIENT)
public class MyStaticClientOnlyEventHandler {
    @SubscribeEvent
    public static void drawLast(RenderLevelStageEvent event) {
        System.out.println("Drawing!");
    }
}
 */

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        Util.registerCommand(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (ConstantStorage.getConfig().isEnableChatBridge()){
            var chatReceiver = EventHandlers.createChatReceiver(event.getServer());

            chatReceiver.setDaemon(true);
            chatReceiver.start();
            ConstantStorage.setChatReceiver(chatReceiver);
        }
        var instructionReceiver = EventHandlers.createInstructionReceiver(event.getServer());
        var sender = new UdpBroadcastSender();
        sender.setDaemon(true);
        instructionReceiver.setDaemon(true);
        instructionReceiver.start();

        sender.start();

        ConstantStorage.setSender(sender);

        ConstantStorage.setInstructionReceiver(instructionReceiver);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ConstantStorage.getSender().setStopped(true);
        if (ConstantStorage.getConfig().isEnableChatBridge())ConstantStorage.getChatReceiver().interrupt();
        ConstantStorage.getInstructionReceiver().interrupt();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (ConstantStorage.getConfig().isEnableChatBridge()) Util.sendChatBroadcast("Joined server %s".formatted(ConstantStorage.getConfig().getControllerName()),event.getEntity().getEntityName());
    }


    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (ConstantStorage.getConfig().isEnableChatBridge()) Util.sendChatBroadcast("Left server %s".formatted(ConstantStorage.getConfig().getControllerName()),event.getEntity().getEntityName());
    }
}
