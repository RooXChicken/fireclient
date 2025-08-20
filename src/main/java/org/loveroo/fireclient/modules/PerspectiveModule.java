package org.loveroo.fireclient.modules;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.ArrayList;
import java.util.List;

public class PerspectiveModule extends ModuleBase {

    private boolean using = false;
    private boolean zoomEnabled = false;

    private float yawOffset = 0.0f;
    private float pitchOffset = 0.0f;
    private float positionOffset = 0.0f;

    public PerspectiveModule() {
        super(new ModuleData("perspective", "\uD83D\uDD0E Perspective", "Allows you to look around in Third Person without moving your player's head"));
        getData().setShownName(generateDisplayName(0x82A5AD));

        getData().setGuiElement(false);

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("use_perspective", Text.of("Use"), Text.of("Use ").copy().append(getData().getShownName()), true, null,
                        this::usePerspectiveKey, () -> using = false)
        );
    }

    private void usePerspectiveKey() {
        if(!getData().isEnabled()) {
            return;
        }

        yawOffset = 0.0f;
        pitchOffset = 0.0f;

        positionOffset = 0.0f;

        using = true;
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
        zoomEnabled = json.optBoolean("zoom_enabled", zoomEnabled);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());
        json.put("zoom_enabled", zoomEnabled);

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_perspective").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(ButtonWidget.builder(getToggleText(Text.of("Zoom In/Out"), zoomEnabled), this::toggleZoomButton)
                .dimensions(base.width/2 - 60, base.height/2 + 20, 120, 20)
                .build());

        return widgets;
    }

    private void toggleZoomButton(ButtonWidget button) {
        zoomEnabled = !zoomEnabled;
        button.setMessage(getToggleText(Text.of("Zoom In/Out"), zoomEnabled));
    }

    public boolean isUsing() {
        return using;
    }

    public void addDelta(float yawDelta, float pitchDelta) {
        yawOffset += yawDelta;
        pitchOffset += pitchDelta;
    }

    public void clampPitch(float current) {
        var pitch = current + pitchOffset;

        if(pitch < -90.0f) {
            pitchOffset = -90.0f - current;
        }

        if(pitch > 90.0f) {
            pitchOffset = 90.0f - current;
        }
    }

    public void addPositionOffset(double offset) {
        positionOffset -= (float)offset*0.2f;
    }

    public void clampPosition(float current) {
        var position = current + positionOffset;
        if(position < 0.2f) {
            positionOffset = 0.2f - current;
        }
    }

    public float getYawOffset() {
        return yawOffset;
    }

    public float getPitchOffset() {
        return pitchOffset;
    }

    public float getPositionOffset() {
        return positionOffset;
    }

    public boolean isZoomEnabled() {
        return zoomEnabled;
    }
}
