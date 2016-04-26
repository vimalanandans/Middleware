package com.bezirk.proxy.api.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the EchoRequest by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */
public class UhuZirkEndPointTest {

    @Test
    public void test() {

        UhuZirkId serviceId = new UhuZirkId("Service25");
        UhuZirkEndPoint uhuServiceEndPoint = new UhuZirkEndPoint(serviceId);
        assertEquals("UhuZirkId is not matching with the set value.", serviceId, uhuServiceEndPoint.getUhuServiceId());

    }

}
