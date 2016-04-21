/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-multiple-choice message (see wiki page for overview)
 * 
 */
package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.Event;
import com.bezirk.Proxy.IndoorLocation;
import com.bezirk.Proxy.ServiceEndPoint;

public class UIMultipleChoiceEventRequest extends Event {
    public static final String MsgLabel = UIMultipleChoiceEventRequest.class.getSimpleName();
    // Payload
    public String[] availableChoices;
    public long expiration;

    /**
     * Multicast
     *
     * @param at     physical location
     * @param sphere
     * @see com.bezirk.sadl.IndoorLocation
     */
    public UIMultipleChoiceEventRequest(IndoorLocation at, String sphere, ServiceEndPoint sender) {
        super(MsgLabel, at, sphere, sender);
    }

    /**
     * Use instead of the generic UhuMessage.deserialize()
     *
     * @param json
     * @return
     */
    public static UIMultipleChoiceEventRequest deserialize(String json) {
        return Event.deserialize(json, UIMultipleChoiceEventRequest.class);
    }

    public void setPayload(String[] availableChoices, long expiration) {
        this.availableChoices = availableChoices;
        this.expiration = expiration;
    }
}
