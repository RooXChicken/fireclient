package org.loveroo.fireclient.settings;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;

import java.util.HashMap;
import java.util.stream.Collectors;

public class PlayerSortPriority {

    private static final HashMap<String, Integer> usages = new HashMap<>();

    public static void register() {
        ClientSendMessageEvents.COMMAND.register((command) -> {
            var players = RooHelper.getNetworkHandler().getPlayerList().stream()
                    .map((entry) -> entry.getProfile().getName().toLowerCase())
                    .collect(Collectors.toUnmodifiableSet());

            var incremented = false;

            for(var arg : command.split(" ")) {
                var lowerArg = arg.toLowerCase();

                if(players.contains(lowerArg)) {
                    incremented = true;
                    incrementUsages(lowerArg);
                }
            }

            if(incremented) {
                FireClientside.saveConfig();
            }
        });
    }

    public static void loadJson(JSONArray json) throws Exception {
        for(var i = 0; i < json.length(); i++) {
            var usageJson = json.optJSONObject(i);
            if(usageJson == null) {
                continue;
            }

            var name = usageJson.optString("name", "");
            var count = usageJson.optInt("count", 0);

            if(!name.isEmpty()) {
                usages.put(name, count);
            }
        }
    }

    public static JSONArray saveJson() throws Exception {
        var json = new JSONArray();

        for(var usage : usages.entrySet()) {
            var usageJson = new JSONObject();

            usageJson.put("name", usage.getKey());
            usageJson.put("count", usage.getValue());

            json.put(usageJson);
        }

        return json;
    }

    public static int getUsages(String name) {
        return usages.getOrDefault(name.toLowerCase(), 0);
    }

    public static void incrementUsages(String name) {
        usages.put(name.toLowerCase(), usages.getOrDefault(name.toLowerCase(), 0) + 1);
    }
}
