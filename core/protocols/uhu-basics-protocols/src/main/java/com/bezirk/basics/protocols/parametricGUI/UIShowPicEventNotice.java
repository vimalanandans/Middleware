/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-show-pic message (see wiki page for overview)
 * 
 */
package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.Event;
import com.bezirk.Proxy.IndoorLocation;
import com.bezirk.Proxy.ServiceEndPoint;


public class UIShowPicEventNotice extends Event {
    public static final String MsgLabel = UIShowPicEventNotice.class.getSimpleName();
    // Payload
    public String picURL;

    /**
     * Multicast
     *
     * @param at     physical location
     * @param sphere
     */
    public UIShowPicEventNotice(IndoorLocation at, String sphere, ServiceEndPoint sender) {
        super(MsgLabel, at, sphere, sender);
    }

    /**
     * Use instead of the generic UhuMessage.deserialize()
     *
     * @param json
     * @return
     */
    public static UIShowPicEventNotice deserialize(String json) {
        return Event.deserialize(json, UIShowPicEventNotice.class);
    }

    public void setPayload(String picURL) {
        this.picURL = picURL;
    }
}
