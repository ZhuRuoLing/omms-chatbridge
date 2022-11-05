package net.zhuruoling.omms.controller.architectury.network;



public class Broadcast {
    String channel;
    String server;
    String player;
    String content;

    public Broadcast(String player, String content) {
        this.server = "";
        this.channel = "";
        this.player = player;
        this.content = content;
    }

    public Broadcast(String channel, String server, String player, String content) {
        this.channel = channel;
        this.server = server;
        this.player = player;
        this.content = content;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
