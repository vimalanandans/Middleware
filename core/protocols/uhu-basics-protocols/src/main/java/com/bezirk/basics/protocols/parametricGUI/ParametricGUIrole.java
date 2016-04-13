package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.ProtocolRole;

/**
 * @author soj1pi
 * @see ProtocolRole
 */
public class ParametricGUIrole implements ProtocolRole {
	
	static final String evts[] =
		{UIShowPicEventNotice.MsgLabel,
		 ShowTextEventNotice.TOPIC,
		 UIShowVideoEventNotice.MsgLabel,
		 UIInputValuesEventRequest.MsgLabel,
		 UIMultipleChoiceEventRequest.MsgLabel,
		 UIPickOneEventRequest.TOPIC};

	@Override
	public String getProtocol() {
		return "ParametricGUI";
	}

	@Override
	public String[] getEvents() {
		return evts;
	}

	@Override
	public String[] getStreams() {
		return null;
	}
}