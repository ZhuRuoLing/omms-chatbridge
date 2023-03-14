package net.zhuruoling.omms.chatbridge.architectury.forge.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.zhuruoling.omms.chatbridge.architectury.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class PlayerChatEventInject {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At("RETURN"), method = "onChatMessage")
    private void handleMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        String raw = packet.chatMessage();
        System.out.println(raw);
        if (!raw.startsWith("/")) {
            Util.sendChatBroadcast(raw, this.player.getGameProfile().getName());
        }
    }
}
