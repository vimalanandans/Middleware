/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-input-values message (see wiki page for overview)
 * 
 */
package com.bosch.upa.UhUbasics.protocols.parametricGUI;

import com.bosch.upa.uhu.Proxy.Event;
import com.bosch.upa.uhu.Proxy.IndoorLocation;
import com.bosch.upa.uhu.Proxy.ServiceEndPoint;

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
     * @see com.bosch.upa.uhu.sadl.IndoorLocation
     */
    public UIInputValuesEventRequest(IndoorLocation at, String sphere, ServiceEndPoint sender) {
        super(MsgLabel, at, sphere, sender);
    }

    /**
     * Use instead of the generic Message.deserialize()
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
