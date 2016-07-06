package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

/**
 * @author Mansimar Aneja
 *         This class is used to generate messageIds for both Events and Control messages on behalf of the Bezirk Stack
 */
public final class GenerateMsgId {

    private static final String KEY_SEPERATOR = ":";
    private static int evtId = 0;
    private static int ctrlId = 0;

    private GenerateMsgId() {
        //Utility class should have a private constructor
    }

    /**
     * This method will be invoked by ProxyForServices to set the msgId of each of the Event going on the wire.
     *
     * @return the msgId
     * Example - 2:192.168.1.124:abc123423
     */
    public static String generateEvtId(BezirkZirkEndPoint sep) {
        if (1024 == evtId) {
            evtId = 0;
        }
        return (++evtId + KEY_SEPERATOR + sep.device + KEY_SEPERATOR + sep.zirkId.getZirkId());
    }

    /**
     * This method will be invoked by ProxyForServices to set the msgId of each of the Event going on the wire.
     *
     * @return the ctrlId
     */
    public static int generateCtrlId() {
        if (1024 == ctrlId) {
            ctrlId = 0;
        }
        return ++ctrlId;
    }
}
