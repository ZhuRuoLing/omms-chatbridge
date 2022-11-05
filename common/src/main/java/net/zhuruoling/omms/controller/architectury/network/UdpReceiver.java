package net.zhuruoling.omms.controller.architectury.network;


import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;


public class UdpReceiver extends Thread{
    private static final Logger logger = LoggerFactory.getLogger("UdpBroadcastReceiver");
    BiConsumer<MinecraftServer, String> function = null;
    UdpBroadcastSender.Target target = null;
    private final MinecraftServer server;
    public UdpReceiver(MinecraftServer server, UdpBroadcastSender.Target target, BiConsumer<MinecraftServer, String> function){
        this.setName("UdpBroadcastReceiver#" + getId());
        this.server = server;
        this.function = function;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            int port = target.port();
            String address = target.address(); // 224.114.51.4:10086
            MulticastSocket socket;
            InetAddress inetAddress;
            inetAddress = InetAddress.getByName(address);
            socket = new MulticastSocket(port);
            logger.info("Started Broadcast Receiver at " + address + ":" + port);
            socket.joinGroup(new InetSocketAddress(inetAddress,port), NetworkInterface.getByInetAddress(inetAddress));
            for (;;) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                    socket.receive(packet);
                    String msg = new String(packet.getData(), packet.getOffset(),
                            packet.getLength(), StandardCharsets.UTF_8);
                    function.accept(this.server,msg);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
/*
java.io.IOException: Network interface not configured for IPv4
        at java.base/sun.nio.ch.DatagramChannelImpl.innerJoin(DatagramChannelImpl.java:1534)
        at java.base/sun.nio.ch.DatagramChannelImpl.join(DatagramChannelImpl.java:1559)
        at java.base/sun.nio.ch.DatagramSocketAdaptor.joinGroup(DatagramSocketAdaptor.java:535)
        at java.base/java.net.DatagramSocket.joinGroup(DatagramSocket.java:1292)
        at java.base/java.net.MulticastSocket.joinGroup(MulticastSocket.java:371)
        at TRANSFORMER/ommscontrollerarchitectury@0.7.0/net.zhuruoling.omms.controller.architectury.network.UdpReceiver.run(UdpReceiver.java:35)
        */
