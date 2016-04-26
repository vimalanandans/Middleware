package com.bezirk.middleware.proxy;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.ZirkId;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author AJC6KOR
 */
class ProxyUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProxyUtil.class);

    void addTopicsToMaps(final ZirkId subscriber,
                         final String[] topics, final BezirkListener listener, HashMap<BezirkZirkId, HashSet<BezirkListener>> sidMap, HashMap<String, HashSet<BezirkListener>> listenerMap, String topicType) {
        for (String topic : topics) {
            //Update Sid Map
            if (sidMap.containsKey((BezirkZirkId) subscriber)) {
                sidMap.get((BezirkZirkId) subscriber).add(listener);
            } else {
                HashSet<BezirkListener> listeners = new HashSet<BezirkListener>();
                listeners.add(listener);
                sidMap.put((BezirkZirkId) subscriber, listeners);
            }
            //Update Event/Stream Map
            if (listenerMap.containsKey(topic)) {
                HashSet<BezirkListener> serviceList = listenerMap.get(topic);
                if (serviceList.contains(listener)) {
                    logger.warn(topicType + " already registered with the Label " + topic);
                } else {
                    serviceList.add(listener);
                }
            } else {
                HashSet<BezirkListener> regServiceList = new HashSet<BezirkListener>();
                regServiceList.add(listener);
                listenerMap.put(topic, regServiceList);
            }
        }
    }

}
