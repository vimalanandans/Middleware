/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-values message (see wiki page for overview)
 * 
 */
package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.Event;
import com.bezirk.Proxy.IndoorLocation;
import com.bezirk.Proxy.ServiceEndPoint;


public class UIValuesEventReply extends Event {
    public static final String MsgLabel = UIValuesEventReply.class.getSimpleName();
    // Payload
    public InputValuesStringPair[] values;

    /**
     * Unicast
     *
     * @param at        physical location
     * @param requestor
     * @param sphere
     * @see com.bezirk.sadl.IndoorLocation
     */
    public UIValuesEventReply(IndoorLocation at, ServiceEndPoint requestor, String sphere, ServiceEndPoint sender) {
        super(MsgLabel, at, requestor, sphere, sender);
    }

    /**
     * Use instead of the generic UhuMessage.deserialize()
     *
     * @param json
     * @return
     */
    public static UIValuesEventReply deserialize(String json) {
        return Event.deserialize(json, UIValuesEventReply.class);
    }

    public void setPayload(InputValuesStringPair[] values) {
        this.values = values;
    }
}
