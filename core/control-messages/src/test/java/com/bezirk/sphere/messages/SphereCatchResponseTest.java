package com.bezirk.sphere.messages;

import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo.UhuDeviceRole;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the SphereCatchResponse by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class SphereCatchResponseTest {

    private static final String SERVICE_A = "ServiceA";

    private static final Logger log = LoggerFactory
            .getLogger(SphereCatchResponseTest.class);

    private static final BezirkZirkId serviceAId = new BezirkZirkId(SERVICE_A);
    private static final BezirkZirkInfo serviceAInfo = new BezirkZirkInfo(serviceAId.getBezirkZirkId(), SERVICE_A, "TEST", true, true);
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceAId);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up SphereCatchResponseTest TestCase *****");
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down SphereCatchResponseTest TestCase *****");
    }


    @Test
    public void testSphereCatchResponse() {

        String catchSphereId = "MemberSphereID";
        String catchDeviceId = "TESTDEVICEID2";
        List<BezirkZirkInfo> serviceList = new ArrayList<BezirkZirkInfo>();
        serviceList.add(serviceAInfo);
        BezirkDeviceInfo services = new BezirkDeviceInfo(catchDeviceId, "TESTDEVICE", "PC", UhuDeviceRole.UHU_MEMBER, true, serviceList);
        com.bezirk.sphere.messages.CatchResponse sphereCatchResonse = new com.bezirk.sphere.messages.CatchResponse(sender, catchSphereId, catchDeviceId, services);
        String serializedMessage = sphereCatchResonse.serialize();
        com.bezirk.sphere.messages.CatchResponse deserializedSphereCatchResponse = com.bezirk.sphere.messages.CatchResponse.deserialize(serializedMessage, com.bezirk.sphere.messages.CatchResponse.class);
        assertEquals("CatchedServices not equal to the set value.", services, deserializedSphereCatchResponse.getInviterSphereDeviceInfo());
        assertEquals("CatchSphereId not equal to the set value.", catchSphereId, deserializedSphereCatchResponse.getCatcherSphereId());
        assertEquals("CatchDeviceId not equal to the set value.", catchDeviceId, deserializedSphereCatchResponse.getCatcherDeviceId());
    }

}
