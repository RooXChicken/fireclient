package org.loveroo.fireclient.data;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashSet;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;

public class Affiliates {
    
    private final HashSet<Affiliate> affiliates = new HashSet<>();

    public void fetchAffiliates() {
        var thread = new FetchAffiliatesThread();
        thread.start();
    }

    public boolean isAffiliate(UUID uuid) {
        return affiliates.stream().anyMatch((it) -> it.uuid().equals(uuid));
    }

    public NametagState getNametagState(UUID uuid) {
        var affiliate = affiliates.stream().filter((it) -> it.uuid().equals(uuid)).toList();
        if(affiliate.isEmpty()) {
            return NametagState.NONE;
        }

        return affiliate.getFirst().nametagState();
    }

    class FetchAffiliatesThread extends Thread {

        @Override
        public void run() {
            try {
                var url = new URI(FireClient.getServerUrl("affiliates/get")).toURL();

                var connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                var code = connection.getResponseCode();

                if(code == 429) {
                    FireClient.LOGGER.info("Failed to get Affiliate list! Rate limited");
                    return;
                }

                var input = connection.getInputStream();
                var receivedData = new String(input.readAllBytes());
                input.close();

                switch(code) {
                    case 400 -> {
                        FireClient.LOGGER.info("Failed to get Affiliate list! {}");
                        return;
                    }
                }

                affiliates.clear();

                var json = new JSONObject(receivedData);
                var array = json.optJSONArray("affiliates");
                if(array == null) {
                    array = new JSONArray();
                }

                for(var i = 0; i < array.length(); i++) {
                    var affiliateJson = array.optJSONObject(i);
                    if(affiliateJson == null) {
                        continue;
                    }

                    var name = affiliateJson.optString("name", "");
                    var uuid = affiliateJson.optString("uuid", "");
                    var nametag = affiliateJson.optInt("nametag_state", 0);

                    if(name.isEmpty() || uuid.isEmpty()) {
                        continue;
                    }

                    var affiliate = new Affiliate(UUID.fromString(uuid), name, NametagState.values()[nametag]);
                    affiliates.add(affiliate);
                }
            }
            catch(Exception e) {
                FireClient.LOGGER.error("Failed to get Affiliate list!", e);
            }
        }
    }

    record Affiliate(UUID uuid, String name, NametagState nametagState) { }

    public static enum NametagState {

        NONE,
        TEXT_COLOR,
        BACKGROUND_COLOR,
    }
}
