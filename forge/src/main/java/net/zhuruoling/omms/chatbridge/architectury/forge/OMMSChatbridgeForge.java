package net.zhuruoling.omms.chatbridge.architectury.forge;

import com.mojang.logging.LogUtils;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.zhuruoling.omms.chatbridge.architectury.OMMSChatbridge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(OMMSChatbridge.MOD_ID)
public class OMMSChatbridgeForge {
    Logger logger = LogUtils.getLogger();
    public OMMSChatbridgeForge() {
        EventBuses.registerModEventBus(OMMSChatbridge.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        OMMSChatbridge.init();
    }

    private void onCommonStartup(FMLCommonSetupEvent event){

    }


}
