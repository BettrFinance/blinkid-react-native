package com.microblink.reactnative.overlays.serialization;

import android.os.Parcelable;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.microblink.reactnative.overlays.OverlaySettingsSerialization;

public final class MultipleScanDocumentActivitySettingsSerialization {
    public Parcelable[] createUISettings(ReadableMap jsonUISettings) {
        ReadableArray config = jsonUISettings.getArray("config");
        Parcelable[] settings = new Parcelable[config.size()];
        for(int i = 0; i < config.size(); i++) {
          ReadableMap configMap = config.getMap(i);
          settings[i] = new MultipleScanSetting(configMap.getString("description"), configMap.getString("overlayImage"), configMap.getString("title"), configMap.getString("successImage"), configMap.getString("successMessage"));
        }

        return settings;
    }

    public String getJsonName() {
        return "MultipleScanDocumentActivitySettings";
    }
}
