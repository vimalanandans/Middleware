/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-pick-one message (see wiki page for overview)
 * 
 */
package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.Event;
import com.bezirk.Proxy.IndoorLocation;
import com.bezirk.Proxy.ServiceEndPoint;

public class UIPickOneEventRequest extends Event {
    public static final String TOPIC = UIPickOneEventRequest.class.getSimpleName();
    // Payload
    public String intro;
    public String[] availableChoices;
    public long expiration;

    /**
     * Multicast
     *
     * @param at     physical location
     * @param sphere
     * @see com.bezirk.pubsubbroker.IndoorLocation
     */
    public UIPickOneEventRequest(IndoorLocation at, String sphere, ServiceEndPoint sender) {
        super(TOPIC, at, sphere, sender);
    }

    /**
     * Use instead of the generic Message.deserialize()
     *
     * @param json
     * @return
     */
    public static UIPickOneEventRequest deserialize(String json) {
        return Event.deserialize(json, UIPickOneEventRequest.class);
    }

    public void setPayload(String intro, String[] availableChoices, long expiration) {
        this.intro = intro;
        this.availableChoices = availableChoices;
        this.expiration = expiration;
    }
}
