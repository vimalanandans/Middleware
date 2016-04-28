package com.bezirk.discovery;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.api.BezirkSphereType;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ajc6kor
 */
public class SphereDiscoveryRecordTest {

    @Test
    public void test() {

        String sphereId = "TestSphere";
        long timeout = 60000;
        int max = 23;
        SphereDiscoveryRecord sphereDiscoveryRecord = new SphereDiscoveryRecord(sphereId, timeout, max);

        assertNotNull("CreationTime is null.", sphereDiscoveryRecord.getCreationTime());
        assertEquals("DiscoveredSetSize is not 0 at start", 0, sphereDiscoveryRecord.getDiscoveredSetSize());
        assertEquals("Max is not equal to the set value.", max, sphereDiscoveryRecord.getMax());
        assertEquals("Timeout is not equal to the set value.", timeout, sphereDiscoveryRecord.getTimeout());
        assertEquals("SphereId is not equal to the set value.", sphereId, sphereDiscoveryRecord.getSphereId());

        assertNotNull("SphereServices is null.", sphereDiscoveryRecord.getSphereZirks());

        BezirkZirkEndPoint bezirkZirkEndPoint = new BezirkZirkEndPoint(new BezirkZirkId("ServiceA"));
        bezirkZirkEndPoint.device = "DeviceA";
        BezirkSphereInfo bezirkSphereInfo = new BezirkSphereInfo("CarSphere12", "CarSphere",
                BezirkSphereType.BEZIRK_SPHERE_TYPE_CAR, null, null);
        sphereDiscoveryRecord.updateSet(bezirkSphereInfo, bezirkZirkEndPoint);


        assertEquals("DiscoveredSetSize is not 1 after updation.", 1, sphereDiscoveryRecord.getDiscoveredSetSize());

    }

}
