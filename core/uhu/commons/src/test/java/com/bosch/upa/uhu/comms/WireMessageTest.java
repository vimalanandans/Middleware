package com.bosch.upa.uhu.comms;

import com.bosch.upa.uhu.processor.WireMessage;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;

public class WireMessageTest {

	@Test
	public void testVersions() {


	WireMessage msg = new WireMessage();
	WireMessage msg2 = new WireMessage();

	String data, data1 ;
	try {

		data = new String (msg.serialize());

		// change the version to initial value
		msg2.setMsgVer("1.2.4");

		data1 = new String(msg2.serialize());

	} catch (IOException e) {
		e.printStackTrace();
		assertFalse(false);
		return;
	}

	assertEquals(msg.getMsgVer(), WireMessage.getVersion(data));

	assertNotEquals(msg.getMsgVer(), WireMessage.getVersion("asd"));

	assertTrue(WireMessage.checkVersion(data));

	assertFalse(WireMessage.checkVersion(data1));




	}

}
