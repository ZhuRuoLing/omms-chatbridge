package net.zhuruoling.omms.controller.architectury.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Objects;

import static net.zhuruoling.omms.controller.architectury.util.Util.invokeHttpGetRequest;


@Mixin(value = net.minecraft.server.PlayerManager.class)
public abstract class PlayerJoinMixin {
    Logger LOGGER = LogUtils.getLogger();

    @Shadow
    @Nullable
    public abstract ServerPlayerEntity getPlayer(String name);

    @Inject(method = "checkCanJoin", at = @At("HEAD"), cancellable = true)
    private void checkCanJoin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        if (!ConstantStorage.getConfig().isEnableWhitelist()) return;
        try {
            String player = profile.getName();
            String url = "http://%s:%d/whitelist/queryAll/%s".formatted(ConstantStorage.getConfig().getHttpQueryAddr(), ConstantStorage.getConfig().getHttpQueryPort(), player);
            String result = invokeHttpGetRequest(url);
            if (result != null){
                if (result.isEmpty()) {
                    var text = Texts.toText(() -> "Cannot auth with OMMS Central server.");
                    cir.setReturnValue(text);
                }
                Gson gson = new GsonBuilder().serializeNulls().create();
                String[] whitelists = gson.fromJson(result, String[].class);
                if (Objects.isNull(whitelists)) {
                    var text = Texts.toText(() -> "Cannot auth with OMMS Central server.");
                    cir.setReturnValue(text);
                }
                if (Arrays.stream(whitelists).toList().contains(ConstantStorage.getConfig().getWhitelistName())) {
                    LOGGER.info("Successfully authed player %s".formatted(player));
                } else {
                    LOGGER.info("Cannot auth player %s".formatted(player));
                    cir.setReturnValue(Texts.toText(() -> "You are not in whitelist."));
                }
            }else {
                cir.setReturnValue(Texts.toText(() -> "Cannot auth with OMMS Central server."));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to parse whitelist server return value.");
            var text = Texts.toText(() -> "Cannot auth with OMMS Central server.");
            cir.setReturnValue(text);
        }
    }
}
