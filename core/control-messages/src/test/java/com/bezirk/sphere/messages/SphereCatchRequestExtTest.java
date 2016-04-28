package com.bezirk.sphere.messages;

import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo.BezirkDeviceRole;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This testCase verifies the SphereCatchRequestExt by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class SphereCatchRequestExtTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SphereCatchRequestExtTest.class);

    private static final BezirkZirkId serviceAId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceAId);
    private static final BezirkZirkId serviceBId = new BezirkZirkId("ServiceB");

    private static final BezirkZirkInfo serviceAInfo = new BezirkZirkInfo(serviceAId.getBezirkZirkId(), "ServiceA", "TESTA", true, true);
    private static final BezirkZirkInfo serviceBInfo = new BezirkZirkInfo(serviceBId.getBezirkZirkId(), "ServiceB", "TESTB", true, true);
    private static List<BezirkZirkInfo> services = new ArrayList<>();
    private static BezirkDeviceInfo bezirkDeviceInfo = null;


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        LOGGER.info("***** Setting up SphereCatchRequestExtTest TestCase *****");
        services.add(serviceAInfo);
        services.add(serviceBInfo);
        bezirkDeviceInfo = new BezirkDeviceInfo("TESTDEVICE", "TEST", "PC", BezirkDeviceRole.UHU_MEMBER, true, services);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        LOGGER.info("***** Shutting down SphereCatchRequestExtTest TestCase *****");
    }


    @Test
    public void testSphereCatchRequestExt() {

        String qrServiceCatchSphereString = "QRSTRING";
        String catchSphereId = "CATCHSPHEREID";
        String scannedTempSphereId = "TEMPID";
        com.bezirk.sphere.messages.CatchRequest sphereCatchRequestExt = new com.bezirk.sphere.messages.CatchRequest(sender, scannedTempSphereId, catchSphereId, bezirkDeviceInfo, qrServiceCatchSphereString);
        String serializedMessage = sphereCatchRequestExt.serialize();
        com.bezirk.sphere.messages.CatchRequest deserializedSphereCatchRequestExt = com.bezirk.sphere.messages.CatchRequest.deserialize(serializedMessage, com.bezirk.sphere.messages.CatchRequest.class);
        assertEquals("SphereID not equal to the set value.", catchSphereId, deserializedSphereCatchRequestExt.getCatcherSphereId());
        assertEquals("UhuDeviceinfo not equal to the set value.", bezirkDeviceInfo, deserializedSphereCatchRequestExt.getBezirkDeviceInfo());
        assertEquals("CatchSphereString not equal to the set value.", qrServiceCatchSphereString, deserializedSphereCatchRequestExt.getSphereExchangeData());

    }


}
