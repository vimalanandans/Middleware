package com.bezirk.control.messages.pipes;

import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

public class PipeUnicastHeader extends PipeHeader {
	
	private UhuServiceEndPoint recipient;
	
	public UnicastHeader toUnicastHeader() {
		UnicastHeader unicastHeader = new UnicastHeader();
		unicastHeader.setRecipient(this.getRecipient());
		unicastHeader.setSenderSEP(this.getSenderSEP());
		unicastHeader.setTopic(this.getTopic());
		
		// TODO: How to set these??
		//unicastHeader.setMessageId(?);
		//unicastHeader.setSphereName(?);

		return unicastHeader;
	}

	public UhuServiceEndPoint getRecipient() {
		return recipient;
	}

	public void setRecipient(UhuServiceEndPoint recipient) {
		this.recipient = recipient;
	}

}
