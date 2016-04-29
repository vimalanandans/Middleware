package com.bezirk.comms;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestBezirkCommsPC {
    @Test
    public void testLoadProperties() {
        try {
            assertNotNull("Properties is null", BezirkCommsPC.loadProperties());
            assertTrue("BezirkCommsPc properties is empty.", BezirkCommsPC.loadProperties().size() > 0);
            assertFalse("BezirkCommsPc properties is empty.", BezirkCommsPC.loadProperties().isEmpty());
            //Following test case will fail if we remove the EmulticastPortVal property or if the port value is set as more than 9999
            int EmulticastPortVal = Integer.valueOf(BezirkCommsPC.loadProperties().getProperty("EMulticastPort"));
            assertTrue("EmulticastPortVal value is different from default value range.", 0 <= EmulticastPortVal && 9999 >= EmulticastPortVal);
        } catch (Exception e) {

            fail("Exception in fetching uhucommsPC properties. " + e.getMessage());

        }
    }


}
