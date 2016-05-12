package com.bezirk.middleware.android.libraries.wifimanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class manage storage of the network data. These preferences are loaded from the passed {@link Context} while initializing {@link WifiManager}
 *
 * @author Rishabh Gulati
 */
class DataManager {
    private static final String TAG = DataManager.class.getCanonicalName();
    private Context context;
    private static final String PREFERENCE_NAME = "wifiConfigs";
    private static final Gson gson = new Gson();

    DataManager(Context context) {
        this.context = context;
    }

    SavedNetwork getSavedNetwork(String networkName) {
        SavedNetwork savedNetwork = null;
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String value = pref.getString(networkName, null);
        if (value != null) {
            savedNetwork = gson.fromJson(value, SavedNetwork.class);
            Log.v(TAG, savedNetwork.toString());
        }
        return savedNetwork;
    }

    boolean saveNetwork(String networkName, String password, WifiManager.SecurityType securityType) {
        SavedNetwork savedNetwork = new SavedNetwork(networkName, securityType, password, false);
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(networkName, gson.toJson(savedNetwork));
        if (editor.commit()) {
            Log.v(TAG, "Network " + networkName + " saved to shared preferences");
            return true;
        }
        Log.e(TAG, "error saving value to storage");
        return false;
    }

    List<SavedNetwork> getSavedNetworks() {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        HashMap<String, String> map = (HashMap<String, String>) pref.getAll();
        List<SavedNetwork> networks = new ArrayList<SavedNetwork>();
        for (String gsonString : map.values()) {
            networks.add(gson.fromJson(gsonString, SavedNetwork.class));
        }
        return networks;
    }

    void clean() {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        pref.edit().clear().commit();
    }

}
