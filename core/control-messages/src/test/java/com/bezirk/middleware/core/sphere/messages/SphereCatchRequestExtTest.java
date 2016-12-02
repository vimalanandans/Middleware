package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo.BezirkDeviceRole;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the SphereCatchRequestExt by retrieving the field values after deserialization.
 */
public class SphereCatchRequestExtTest {
    private static final Logger logger = LoggerFactory.getLogger(SphereCatchRequestExtTest.class);

    private static final ZirkId serviceAId = new ZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceAId);
    private static final ZirkId serviceBId = new ZirkId("ServiceB");

    private static final BezirkZirkInfo serviceAInfo = new BezirkZirkInfo(serviceAId.getZirkId(), "ServiceA", "TESTA", true, true);
    private static final BezirkZirkInfo serviceBInfo = new BezirkZirkInfo(serviceBId.getZirkId(), "ServiceB", "TESTB", true, true);
    private static List<BezirkZirkInfo> services = new ArrayList<>();
    private static BezirkDeviceInfo bezirkDeviceInfo = null;


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereCatchRequestExtTest TestCase *****");
        services.add(serviceAInfo);
        services.add(serviceBInfo);
        bezirkDeviceInfo = new BezirkDeviceInfo("TESTDEVICE", "TEST", "PC", BezirkDeviceRole.BEZIRK_MEMBER, true, services);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereCatchRequestExtTest TestCase *****");
    }


    @Test
    public void testSphereCatchRequestExt() {
        String qrServiceCatchSphereString = "QRSTRING";
        String catchSphereId = "CATCHSPHEREID";
        String scannedTempSphereId = "TEMPID";
        com.bezirk.middleware.core.sphere.messages.CatchRequest sphereCatchRequestExt = new com.bezirk.middleware.core.sphere.messages.CatchRequest(sender, scannedTempSphereId, catchSphereId, bezirkDeviceInfo, qrServiceCatchSphereString);
        String serializedMessage = sphereCatchRequestExt.serialize();
        com.bezirk.middleware.core.sphere.messages.CatchRequest deserializedSphereCatchRequestExt = com.bezirk.middleware.core.sphere.messages.CatchRequest.deserialize(serializedMessage, com.bezirk.middleware.core.sphere.messages.CatchRequest.class);
        assertEquals("SphereId not equal to the set value.", catchSphereId, deserializedSphereCatchRequestExt.getCatcherSphereId());
        assertEquals("BezirkDeviceInfo not equal to the set value.", bezirkDeviceInfo, deserializedSphereCatchRequestExt.getBezirkDeviceInfo());
        assertEquals("CatchSphereString not equal to the set value.", qrServiceCatchSphereString, deserializedSphereCatchRequestExt.getSphereExchangeData());
    }
}
