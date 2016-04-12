package com.bezirk.proxy;

import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.IBezirkListener;
import com.bezirk.api.addressing.ServiceId;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * @author AJC6KOR
 *
 */
class ProxyUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyUtil.class);

    
    void addTopicsToMaps(final ServiceId subscriber,
            final String[] topics, final IBezirkListener listener, HashMap<UhuServiceId,HashSet<IBezirkListener>> sidMap, HashMap<String, HashSet<IBezirkListener>> listenerMap, String topicType) {
        for (String topic : topics) {
            //Update Sid Map
            if(sidMap.containsKey((UhuServiceId) subscriber)){
                sidMap.get((UhuServiceId) subscriber).add(listener);
            }
            else{
                HashSet<IBezirkListener> listeners = new HashSet<IBezirkListener>();
                listeners.add(listener);
                sidMap.put((UhuServiceId) subscriber, listeners);
            }
            //Update Event/Stream Map
            if (listenerMap.containsKey(topic)) {
                HashSet<IBezirkListener> serviceList = listenerMap.get(topic);
                if (serviceList.contains(listener)) {
                    LOGGER.warn(topicType+" already registered with the Label " + topic);
                } else {
                    serviceList.add(listener);
                }
            } else {
                HashSet<IBezirkListener> regServiceList = new HashSet<IBezirkListener>();
                regServiceList.add(listener);
                listenerMap.put(topic, regServiceList);
            }
        }
    }

}
