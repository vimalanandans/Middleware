package com.bezirk.proxy.android;

import android.util.Log;

import com.bezirk.middleware.BezirkListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AJC6KOR on 11/19/2015.
 */
public class ProxyHelper {

    static final String TAG = ProxyHelper.class.getSimpleName();

    void addTopicsToMap(String[] topics, Map<String, ArrayList<BezirkListener>> listenerMap, BezirkListener listener, String type){
        if (StringValidatorUtil.areValidStrings(topics)) {
            for (String topic : topics) {
                if (listenerMap.containsKey(topic)) {
                    addListener(listenerMap, listener, type, topic);
                } else {
                    List<BezirkListener> regServiceList = new ArrayList<BezirkListener>();
                    regServiceList.add(listener);
                    listenerMap.put(topic, (ArrayList)regServiceList);
                }
            }
        } else {
            Log.i(TAG, "No "+type+" to Subscribe");
        }
    }

    private void addListener(Map<String, ArrayList<BezirkListener>> listenerMap, BezirkListener listener, String type, String topic) {
        List<BezirkListener> serviceList = listenerMap.get(topic);
        if (serviceList.contains(listener)) {
            Log.w(TAG, type + " already registered with the " + type + "Label " + topic);
        } else {
            serviceList.add(listener);
        }
    }
}
