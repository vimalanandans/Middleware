/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-choice message (see wiki page for overview)
 * 
 */
package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.Event;
import com.bezirk.Proxy.IndoorLocation;
import com.bezirk.Proxy.ServiceEndPoint;

public class UIChoiceEventReply extends Event {
    public static final String MsgLabel = UIChoiceEventReply.class.getSimpleName();
    // Payload
    public int[] selectedChoices;

    /**
     * Unicast
     *
     * @param at        physical location
     * @param requestor
     * @param sphere
     * @see com.bezirk.pubsubbroker.IndoorLocation
     */
    public UIChoiceEventReply(IndoorLocation at, ServiceEndPoint requestor, String sphere, ServiceEndPoint sender) {
        super(MsgLabel, at, requestor, sphere, sender);
    }

    /**
     * Use instead of the generic Message.deserialize()
     *
     * @param json
     * @return
     */
    public static UIChoiceEventReply deserialize(String json) {
        return Event.deserialize(json, UIChoiceEventReply.class);
    }

    public void setPayload(int[] selectedChoices) {
        this.selectedChoices = selectedChoices;
    }
}
