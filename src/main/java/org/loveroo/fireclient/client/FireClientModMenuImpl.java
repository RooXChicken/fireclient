package org.loveroo.fireclient.client;

import org.loveroo.fireclient.screen.config.MainConfigScreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class FireClientModMenuImpl implements ModMenuApi {
    
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (screen) -> new MainConfigScreen();
	}
}
