package org.loveroo.fireclient.modules;

import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.ModuleData;

public class ShadowModule extends ModuleBase {

    public ShadowModule() {
        super(new ModuleData("shadow", "\uD83D\uDD73 Shadows", "Modifies shadows"));

        getData().setSelectable(false);
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());

        return json;
    }
}
