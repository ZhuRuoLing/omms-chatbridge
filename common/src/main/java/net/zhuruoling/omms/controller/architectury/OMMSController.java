package net.zhuruoling.omms.controller.architectury;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.Registries;

import java.util.function.Supplier;

public class OMMSController {
    public static final String MOD_ID = "omms-controller-architectury";
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    public static void init() {
        System.out.println(OMMSControllerExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
