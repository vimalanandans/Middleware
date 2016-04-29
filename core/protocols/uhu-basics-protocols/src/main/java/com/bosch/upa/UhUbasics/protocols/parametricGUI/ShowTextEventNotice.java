/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-show-text message (see wiki page for overview)
 * 
 */
package com.bosch.upa.UhUbasics.protocols.parametricGUI;

import com.bosch.upa.uhu.Proxy.Event;
import com.bosch.upa.uhu.Proxy.IndoorLocation;
import com.bosch.upa.uhu.Proxy.ServiceEndPoint;

/**
 * A notice Event that instructs the device to display the specified text
 * ShowTextEventNotice
 */

public class ShowTextEventNotice extends Event {
    public static final String TOPIC = ShowTextEventNotice.class.getSimpleName();
    private String text;

    ;
    private TextType type;
    private long expiration;
    /**
     * Multicast
     *
     * @param at     physical location
     * @param sphere
     */
    public ShowTextEventNotice(IndoorLocation at, String sphere, ServiceEndPoint sender) {
        super(TOPIC, at, sphere, sender);
    }

    /**
     * Use instead of the generic Message.deserialize()
     *
     * @param json
     * @return
     */
    public static ShowTextEventNotice deserialize(String json) {
        return Event.deserialize(json, ShowTextEventNotice.class);
    }

    public void setPayload(String text, TextType type, long expiration) {
        this.text = text;
        this.type = type;
        this.expiration = expiration;
    }

    public String getText() {
        return text;
    }

    public TextType getType() {
        return type;
    }

    public long getExpiration() {
        return expiration;
    }

    // Payload
    public enum TextType {
        INFORMATION, WARNING, ERROR
    }
}
