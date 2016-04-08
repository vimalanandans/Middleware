/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-choice message (see wiki page for overview)
 * 
 */
package com.bosch.upa.UhUbasics.protocols.parametricGUI;

import com.bosch.upa.uhu.Proxy.ServiceEndPoint;
import com.bosch.upa.uhu.Proxy.Event;
import com.bosch.upa.uhu.Proxy.IndoorLocation;

public class UIChoiceEventReply extends Event {
	public static final String MsgLabel = UIChoiceEventReply.class.getSimpleName();
	// Payload
	public int[] selectedChoices;
	
	/**
	 * Unicast
	 * 
	 * @param at physical location
	 * @param requestor
	 * @param sphere
	 * @see com.bosch.upa.uhu.sadl.IndoorLocation
	 */
	public UIChoiceEventReply(IndoorLocation at, ServiceEndPoint requestor, String sphere, ServiceEndPoint sender) {
		super(MsgLabel, at, requestor, sphere, sender);
	}
	
	public void setPayload(int[] selectedChoices) {
		this.selectedChoices = selectedChoices;
	}
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static UIChoiceEventReply deserialize(String json) {
		return Event.deserialize(json, UIChoiceEventReply.class);
	}
}
