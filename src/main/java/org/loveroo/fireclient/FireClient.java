package org.loveroo.fireclient;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FireClient implements ModInitializer {

    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static String MOD_ID = "fireclient";
    public static String KEYBIND_CATEGORY = "key.category.fireclient";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

    }
}
