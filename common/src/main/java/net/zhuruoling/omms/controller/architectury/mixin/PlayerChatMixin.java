package net.zhuruoling.omms.controller.architectury.mixin;


import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import net.zhuruoling.omms.controller.architectury.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//owe great thanks to Gugle and his SuperEvent mod
@Mixin(value = net.minecraft.server.network.ServerPlayNetworkHandler.class)
public class PlayerChatMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At("RETURN"), method = "onChatMessage")
    private void handleMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (!ConstantStorage.getConfig().isEnableChatBridge())return;
        String raw = packet.chatMessage();
        //System.out.println(raw);
        if (!raw.startsWith("/")) {
            Util.sendChatBroadcast(raw, this.player.getName().getString());
        }
    }
    /*
    [14:44:54] [Netty Server IO #2/INFO]: [STDOUT]: 囸你仙人
[14:44:54] [Server thread/INFO]: [Not Secure] <ZhuRuoLing> 囸你仙人
[14:44:54] [UdpBroadcastReceiver#71/INFO]: GLOBAL <ZhuRuoLing[omms-controller]> 囸你仙人
[14:44:54] [UdpBroadcastReceiver#71/INFO]: GLOBAL <ZhuRuoLing[omms-controller]> 囸你仙人
     */
}
