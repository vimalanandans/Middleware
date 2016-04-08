/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-multiple-choice message (see wiki page for overview)
 * 
 */
package com.bosch.upa.UhUbasics.protocols.parametricGUI;

import com.bosch.upa.uhu.Proxy.Event;
import com.bosch.upa.uhu.Proxy.ServiceEndPoint;
import com.bosch.upa.uhu.Proxy.IndoorLocation;

public class UIMultipleChoiceEventRequest extends Event {
	public static final String MsgLabel = UIMultipleChoiceEventRequest.class.getSimpleName();
	// Payload
	public String[] availableChoices;
	public long expiration;
	
	/**
	 * Multicast
	 * 
	 * @param at physical location
	 * @param sphere
	 * @see com.bosch.upa.uhu.sadl.IndoorLocation
	 */
	public UIMultipleChoiceEventRequest(IndoorLocation at, String sphere, ServiceEndPoint sender) {
		super(MsgLabel, at, sphere, sender);
	}
	
	public void setPayload(String[] availableChoices, long expiration) {
		this.availableChoices = availableChoices;
		this.expiration = expiration;
	}
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static UIMultipleChoiceEventRequest deserialize(String json) {
		return Event.deserialize(json, UIMultipleChoiceEventRequest.class);
	}
}
