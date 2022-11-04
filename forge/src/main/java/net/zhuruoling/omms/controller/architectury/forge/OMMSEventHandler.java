package net.zhuruoling.omms.controller.architectury.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
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
    @SubscribeEvent

    public void onServerStarted(ServerStartedEvent event) {
        var chatReceiver = EventHandlers.createChatReceiver(event.getServer());
        var instructionReceiver = EventHandlers.createInstructionReceiver(event.getServer());
        var sender = new UdpBroadcastSender();
        chatReceiver.setDaemon(true);
        sender.setDaemon(true);
        instructionReceiver.setDaemon(true);
        instructionReceiver.start();

        sender.start();
        chatReceiver.start();

        ConstantStorage.setSender(sender);
        ConstantStorage.setChatReceiver(chatReceiver);
        ConstantStorage.setInstructionReceiver(instructionReceiver);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ConstantStorage.getSender().setStopped(true);
        ConstantStorage.getChatReceiver().interrupt();
        ConstantStorage.getInstructionReceiver().interrupt();
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Util.sendChatBroadcast("Joined server %s".formatted(ConstantStorage.getConfig().getControllerName()),event.getEntity().getEntityName());
    }


    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Util.sendChatBroadcast("Left server %s".formatted(ConstantStorage.getConfig().getControllerName()),event.getEntity().getEntityName());
    }
}
