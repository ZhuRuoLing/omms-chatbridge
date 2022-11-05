package net.zhuruoling.omms.controller.architectury;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.registries.Registries;
import net.zhuruoling.omms.controller.architectury.config.Config;
import net.zhuruoling.omms.controller.architectury.config.ConstantStorage;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class OMMSController {
    private static final Logger logger = LogUtils.getLogger();
    public static final String MOD_ID = "ommscontrollerarchitectury";
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    public static void init() {
        String s = OMMSControllerExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString();
        logger.info(s);
        ConstantStorage.setConfig(new Config().load());
        logger.info(ConstantStorage.getConfig().toString());
    }
}
