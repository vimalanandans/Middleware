package com.bezirk.proxy.api.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the EchoRequest by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */
public class BezirkZirkEndPointTest {

    @Test
    public void test() {

        BezirkZirkId zirkId = new BezirkZirkId("Zirk25");
        BezirkZirkEndPoint bezirkZirkEndPoint = new BezirkZirkEndPoint(zirkId);
        assertEquals("BezirkZirkId is not matching with the set value.", zirkId, bezirkZirkEndPoint.getBezirkZirkId());

    }

}
