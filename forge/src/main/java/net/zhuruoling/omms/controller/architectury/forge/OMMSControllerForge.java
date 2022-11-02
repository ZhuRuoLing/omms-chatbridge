package net.zhuruoling.omms.controller.architectury.forge;

import dev.architectury.platform.forge.EventBuses;
import net.zhuruoling.omms.controller.architectury.OMMSController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OMMSController.MOD_ID)
public class OMMSControllerForge {
    public OMMSControllerForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(OMMSController.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        OMMSController.init();
    }
}
