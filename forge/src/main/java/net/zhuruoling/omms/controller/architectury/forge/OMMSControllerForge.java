package net.zhuruoling.omms.controller.architectury.forge;

import com.mojang.logging.LogUtils;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.zhuruoling.omms.controller.architectury.OMMSController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.zhuruoling.omms.controller.architectury.OMMSControllerExpectPlatform;
import org.slf4j.Logger;

@Mod(OMMSController.MOD_ID)
public class OMMSControllerForge {
    Logger logger = LogUtils.getLogger();
    public OMMSControllerForge() {
        EventBuses.registerModEventBus(OMMSController.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        OMMSController.init();
    }

    private void onCommonStartup(FMLCommonSetupEvent event){

    }


}
