package org.loveroo.fireclient;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FireClient implements ModInitializer {

    static {
        // attempt to override headless mode
        System.setProperty("java.awt.headless", "false");
    }

    public static final String MOD_ID = "fireclient";
    public static final String FIRECLIENT_VERSION = "1.3.2";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String KEYBIND_CATEGORY = "key.category.fireclient";

    private static final String SERVER_URL = "https://api.loveroo.org/api/fireclient/";

    @Override
    public void onInitialize() {

    }

    public static String getServerUrl(String path) {
        return SERVER_URL + path;
    }
}
