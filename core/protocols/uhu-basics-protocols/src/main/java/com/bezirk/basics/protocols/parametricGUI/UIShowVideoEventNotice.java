/*
 * @author: Marcelo Cataldo (CR/RTC3-NA)
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This class implements the ui-show-video message (see wiki page for overview)
 * 
 */
package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.Event;
import com.bezirk.Proxy.ServiceEndPoint;
import com.bezirk.Proxy.IndoorLocation;

public class UIShowVideoEventNotice extends Event {
	public static final String MsgLabel = UIShowVideoEventNotice.class.getSimpleName();
	// Payload
	public String videoURL;
	
	/**
	 * Multicast
	 * 
	 * @param at physical location
	 * @param sphere
	 */
	public UIShowVideoEventNotice(IndoorLocation at, String sphere, ServiceEndPoint sender) {
		super(MsgLabel, at, sphere, sender);
	}
	
	public void setPayload(String videoURL) {
		this.videoURL = videoURL;
	}
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static UIShowVideoEventNotice deserialize(String json) {
		return Event.deserialize(json, UIShowVideoEventNotice.class);
	}
}