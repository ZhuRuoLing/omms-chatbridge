package net.zhuruoling.omms.chatbridge.architectury.network;


public class Broadcast {
    String server;
    String displayName;
    String content;
    String id;

    BroadcastType broadcastType;

    public Broadcast(String displayName, String content, String id, BroadcastType broadcastType) {
        server = "Forge Minecraft Server";
        this.displayName = displayName;
        this.content = content;
        this.id = id;
        this.broadcastType = broadcastType;
    }

    public BroadcastType getBroadcastType() {
        return broadcastType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getServer() {
        return server;
    }
}
