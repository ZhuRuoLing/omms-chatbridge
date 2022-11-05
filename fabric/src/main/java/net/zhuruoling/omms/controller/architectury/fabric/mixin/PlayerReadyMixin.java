package net.zhuruoling.omms.controller.architectury.fabric.mixin;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.zhuruoling.omms.controller.architectury.util.Util.invokeHttpGetRequest;


@Mixin(PlayerManager.class)
public class PlayerReadyMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect",at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        if (!ConstantStorage.getConfig().isEnableWhitelist())return;
        String playerName = player.getName().copyContentOnly().getString();
        String url = "http://%s:%d/whitelist/queryAll/%s".formatted(ConstantStorage.getConfig().getHttpQueryAddr(), ConstantStorage.getConfig().getHttpQueryPort(),playerName);
        String result = invokeHttpGetRequest(url);
        NbtCompound compound = new NbtCompound();
        compound.putString("servers", result);
        var dispatcher = Objects.requireNonNull(player.getServer()).getCommandManager().getDispatcher();

        Objects.requireNonNull(player.getServer()).getCommandManager().execute(
                dispatcher.parse(
                        "menu %s".formatted(compound.asString())
                        , player.getCommandSource()
                )
                ,"menu %s".formatted(compound.asString())
        );
    }
}
