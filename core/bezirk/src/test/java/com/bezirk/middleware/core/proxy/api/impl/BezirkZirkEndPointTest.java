package com.bezirk.middleware.core.proxy.api.impl;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

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

        ZirkId zirkId = new ZirkId("Zirk25");
        BezirkZirkEndPoint bezirkZirkEndPoint = new BezirkZirkEndPoint(zirkId);
        assertEquals("ZirkId is not matching with the set value.", zirkId, bezirkZirkEndPoint.getBezirkZirkId());

    }

}
