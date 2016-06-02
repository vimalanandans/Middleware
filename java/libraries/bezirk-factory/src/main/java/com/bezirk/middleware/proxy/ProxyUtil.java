package com.bezirk.middleware.proxy;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.proxy.api.impl.ZirkId;

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
                         final String[] topics, final BezirkListener listener, HashMap<ZirkId, HashSet<BezirkListener>> sidMap, HashMap<String, HashSet<BezirkListener>> listenerMap, String topicType) {
        for (String topic : topics) {
            //Update Sid Map
            if (sidMap.containsKey(subscriber)) {
                sidMap.get(subscriber).add(listener);
            } else {
                HashSet<BezirkListener> listeners = new HashSet<BezirkListener>();
                listeners.add(listener);
                sidMap.put(subscriber, listeners);
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
