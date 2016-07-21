package com.bezirk.middleware.proxy.android;

import android.util.Log;

import com.bezirk.middleware.BezirkListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProxyClientHelper {

    static final String TAG = ProxyClientHelper.class.getSimpleName();

    void addTopicsToMap(String[] topics, Map<String, List<BezirkListener>> listenerMap, BezirkListener listener, String type) {
        for (String topic : topics) {
            if (topic == null || topic.isEmpty()) {
                continue;
            }

            if (listenerMap.containsKey(topic)) {
                addListener(listenerMap, listener, type, topic);
            } else {
                List<BezirkListener> regServiceList = new ArrayList<BezirkListener>();
                regServiceList.add(listener);
                listenerMap.put(topic, regServiceList);
            }
        }
    }

    private void addListener(Map<String, List<BezirkListener>> listenerMap, BezirkListener listener, String type, String topic) {
        List<BezirkListener> zirkList = listenerMap.get(topic);
        if (zirkList.contains(listener)) {
            Log.w(TAG, type + " already registered with the " + type + "Label " + topic);
        } else {
            zirkList.add(listener);
        }
    }
}
