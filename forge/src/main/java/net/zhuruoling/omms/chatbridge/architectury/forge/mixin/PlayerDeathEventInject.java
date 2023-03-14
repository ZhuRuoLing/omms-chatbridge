package net.zhuruoling.omms.chatbridge.architectury.forge.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.zhuruoling.omms.chatbridge.architectury.network.BroadcastType;
import net.zhuruoling.omms.chatbridge.architectury.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
abstract public class PlayerDeathEventInject {

    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Inject(method = "onDeath", at = @At("HEAD"))
    void inj(DamageSource damageSource, CallbackInfo ci){
        Text text = damageSource.getDeathMessage(this.networkHandler.player);
        Util.sendBroadcast(text.getString(), networkHandler.player.getGameProfile().getName(), BroadcastType.DEATH);
    }
}
