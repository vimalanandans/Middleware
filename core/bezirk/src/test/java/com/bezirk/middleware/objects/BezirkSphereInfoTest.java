package com.bezirk.middleware.objects;

import com.bezirk.middleware.objects.BezirkDeviceInfo.BezirkDeviceRole;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This testcase verifies the BezirkSphereInfo by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */
public class BezirkSphereInfoTest {

    @Test
    public void test() {

        String sphereID = "Sphere24";
        String sphereName = "HomeSphere";
        String sphereType = "OwnerSphere";
        ArrayList<BezirkDeviceInfo> deviceList = getDeviceList();
        BezirkSphereInfo bezirkSphereInfo = new BezirkSphereInfo(sphereID, sphereName,
                sphereType, deviceList);
        bezirkSphereInfo.setThisDeviceOwnsSphere(true);

        assertEquals("DeviceList is not equal to the set value.", deviceList, bezirkSphereInfo.getDeviceList());
        assertEquals("SphereID is not equal to the set value.", sphereID, bezirkSphereInfo.getSphereID());
        assertEquals("SphereName is not equal to the set value.", sphereName, bezirkSphereInfo.getSphereName());
        assertEquals("SphereType is not equal to the set value.", sphereType, bezirkSphereInfo.getSphereType());
        assertTrue("Device is not considered as owner of sphere.", bezirkSphereInfo.isThisDeviceOwnsSphere());

        sphereName = "TestSphere";
        BezirkSphereInfo bezirkSphereInfoTemp = new BezirkSphereInfo(sphereID, sphereName,
                sphereType, deviceList);
        assertFalse("Different bezirkSphereInfo has same string representation.", bezirkSphereInfo.toString().equalsIgnoreCase(bezirkSphereInfoTemp.toString()));
    }

    private ArrayList<BezirkDeviceInfo> getDeviceList() {
        String deviceId = "Device123";
        String deviceName = "DeviceA";
        String deviceType = "PC";
        BezirkDeviceRole deviceRole = BezirkDeviceRole.BEZIRK_MEMBER;
        boolean deviceActive = true;
        String zirkName = "ZirkA";
        String zirkId = "Zirk123";
        String zirkType = "MemberZirk";
        boolean active = true;
        boolean visible = true;
        BezirkZirkInfo bezirkZirkInfo = new BezirkZirkInfo(zirkId, zirkName, zirkType, active, visible);
        List<BezirkZirkInfo> zirks = new ArrayList<>();
        zirks.add(bezirkZirkInfo);

        BezirkDeviceInfo bezirkDeviceInfo = new BezirkDeviceInfo(deviceId, deviceName,
                deviceType, deviceRole, deviceActive, zirks);

        ArrayList<BezirkDeviceInfo> deviceList = new ArrayList<BezirkDeviceInfo>();
        deviceList.add(bezirkDeviceInfo);

        return deviceList;
    }

}
