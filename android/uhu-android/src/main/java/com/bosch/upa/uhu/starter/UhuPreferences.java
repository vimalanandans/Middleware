package com.bosch.upa.uhu.starter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by vnd2kor on 12/29/2014.
 * <p/>
 * Uhu Preference storing the setting for keeping all the android way of storing the preferences
 */
public class UhuPreferences {

    private SharedPreferences preferences;

    public static final String DEVICE_ID_TAG_PREFERENCE = "DeviceId";

    public static final String DEVICE_TYPE_TAG_PREFERENCE = "DeviceType";

    public static final String DEVICE_NAME_TAG_PREFERENCE = "DeviceName";

    public static final String DEFAULT_SPHERE_NAME_TAG_PREFERENCE = "DefaultSphereName";

    private final Logger log = LoggerFactory.getLogger(UhuPreferences.class);

    public UhuPreferences(Context context) {
        if (context != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Map<String,?> keys = preferences.getAll();

            for(Map.Entry<String,?> entry : keys.entrySet()){
                log.debug("map values "+ entry.getKey() + ": " +
                        entry.getValue().toString());
            }
        }

    }

    /**
     * return the preference for UhuComm
     */
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }

    /**
     * get the value for the key
     */
    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    /* check if a preference is present*/
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    /**
     * pet the value for the key
     */
    public boolean putString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(key, value);

        editor.commit();

        return true;
    }

}
