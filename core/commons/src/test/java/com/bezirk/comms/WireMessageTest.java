package com.bezirk.comms;

import com.bezirk.comms.processor.WireMessage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class WireMessageTest {

    @Test
    public void testVersions() {


        WireMessage msg = new WireMessage();
        WireMessage msg2 = new WireMessage();

        String data, data1;

        data = new String(msg.serialize());

        // change the version to initial value
        msg2.setMsgVer("1.2.4");

        data1 = new String(msg2.serialize());

        assertEquals(msg.getMsgVer(), WireMessage.getVersion(data));

        assertNotEquals(msg.getMsgVer(), WireMessage.getVersion("asd"));

        assertTrue(WireMessage.checkVersion(data));

        assertFalse(WireMessage.checkVersion(data1));


    }

}
