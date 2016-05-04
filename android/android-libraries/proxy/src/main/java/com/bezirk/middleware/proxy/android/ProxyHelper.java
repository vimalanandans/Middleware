package com.bezirk.middleware.proxy.android;

import android.util.Log;

import com.bezirk.middleware.BezirkListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProxyHelper {

    static final String TAG = ProxyHelper.class.getSimpleName();

    void addTopicsToMap(String[] topics, Map<String, List<BezirkListener>> listenerMap, BezirkListener listener, String type) {
        if (StringValidatorUtil.areValidStrings(topics)) {
            for (String topic : topics) {
                if (listenerMap.containsKey(topic)) {
                    addListener(listenerMap, listener, type, topic);
                } else {
                    List<BezirkListener> regServiceList = new ArrayList<BezirkListener>();
                    regServiceList.add(listener);
                    listenerMap.put(topic, regServiceList);
                }
            }
        } else {
            Log.i(TAG, "No " + type + " to Subscribe");
        }
    }

    private void addListener(Map<String, List<BezirkListener>> listenerMap, BezirkListener listener, String type, String topic) {
        List<BezirkListener> serviceList = listenerMap.get(topic);
        if (serviceList.contains(listener)) {
            Log.w(TAG, type + " already registered with the " + type + "Label " + topic);
        } else {
            serviceList.add(listener);
        }
    }
}
