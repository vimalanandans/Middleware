/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-input-values message (see wiki page for overview)
 * 
 */
package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.Event;
import com.bezirk.Proxy.IndoorLocation;
import com.bezirk.Proxy.ServiceEndPoint;

public class UIInputValuesEventRequest extends Event {
    public static final String MsgLabel = UIInputValuesEventRequest.class.getSimpleName();
    // Payload
    public InputValuesStringTriplet[] values;
    public long expiration;

    /**
     * Multicast
     *
     * @param at     physical location
     * @param sphere
     * @see com.bezirk.sadl.IndoorLocation
     */
    public UIInputValuesEventRequest(IndoorLocation at, String sphere, ServiceEndPoint sender) {
        super(MsgLabel, at, sphere, sender);
    }

    /**
     * Use instead of the generic UhuMessage.deserialize()
     *
     * @param json
     * @return
     */
    public static UIInputValuesEventRequest deserialize(String json) {
        return Event.deserialize(json, UIInputValuesEventRequest.class);
    }

    public void setPayload(InputValuesStringTriplet[] values, long expiration) {
        this.values = values;
        this.expiration = expiration;
    }
}
