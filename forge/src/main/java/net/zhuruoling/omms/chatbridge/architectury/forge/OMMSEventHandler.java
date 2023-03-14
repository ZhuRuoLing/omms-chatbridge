package net.zhuruoling.omms.chatbridge.architectury.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zhuruoling.omms.chatbridge.architectury.OMMSChatbridge;
import net.zhuruoling.omms.chatbridge.architectury.config.ConstantStorage;
import net.zhuruoling.omms.chatbridge.architectury.events.EventHandlers;
import net.zhuruoling.omms.chatbridge.architectury.network.Broadcast;
import net.zhuruoling.omms.chatbridge.architectury.network.BroadcastType;
import net.zhuruoling.omms.chatbridge.architectury.network.UdpBroadcastSender;
import net.zhuruoling.omms.chatbridge.architectury.util.Util;

@Mod.EventBusSubscriber(modid = OMMSChatbridge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class OMMSEventHandler {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        var chatReceiver = EventHandlers.createChatReceiver(event.getServer());
        chatReceiver.setDaemon(true);
        chatReceiver.start();
        ConstantStorage.setChatReceiver(chatReceiver);
        var sender = new UdpBroadcastSender();
        sender.setDaemon(true);
        sender.start();
        ConstantStorage.setSender(sender);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ConstantStorage.getSender().setStopped(true);
        ConstantStorage.getChatReceiver().interrupt();
    }

    @SubscribeEvent
    public static void onPlayerGotAdvancement(AdvancementEvent event) {
        if (event.getAdvancement().getDisplay() == null) return;
        Util.sendAdvancementBroadcast(event.getAdvancement(), event.getEntity().getGameProfile().getName());
    }
}

