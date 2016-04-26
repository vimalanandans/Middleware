package com.bezirk.middleware.objects;

import com.bezirk.middleware.objects.BezirkDeviceInfo.UhuDeviceRole;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This testcase verifies the UhuSphereInfo by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */
public class UhuSphereInfoTest {

    @Test
    public void test() {

        String sphereID = "Sphere24";
        String sphereName = "HomeSphere";
        String sphereType = "OwnerSphere";
        ArrayList<BezirkDeviceInfo> deviceList = getDeviceList();
        ArrayList<com.bezirk.middleware.objects.UhuPipeInfo> pipeList = getPipeList();
        com.bezirk.middleware.objects.UhuSphereInfo uhuSphereInfo = new com.bezirk.middleware.objects.UhuSphereInfo(sphereID, sphereName,
                sphereType, deviceList, pipeList);
        uhuSphereInfo.setThisDeviceOwnsSphere(true);

        assertEquals("DeviceList is not equal to the set value.", deviceList, uhuSphereInfo.getDeviceList());
        assertEquals("PipeList is not equal to the set value.", pipeList, uhuSphereInfo.getPipeList());
        assertEquals("SphereID is not equal to the set value.", sphereID, uhuSphereInfo.getSphereID());
        assertEquals("SphereName is not equal to the set value.", sphereName, uhuSphereInfo.getSphereName());
        assertEquals("SphereType is not equal to the set value.", sphereType, uhuSphereInfo.getSphereType());
        assertTrue("Device is not considered as owner of sphere.", uhuSphereInfo.isThisDeviceOwnsSphere());

        sphereName = "TestSphere";
        com.bezirk.middleware.objects.UhuSphereInfo uhuSphereInfoTemp = new UhuSphereInfo(sphereID, sphereName,
                sphereType, deviceList, pipeList);
        assertFalse("Different uhuSphereInfo has same string representation.", uhuSphereInfo.toString().equalsIgnoreCase(uhuSphereInfoTemp.toString()));
    }


    private ArrayList<com.bezirk.middleware.objects.UhuPipeInfo> getPipeList() {

        String pipeId = "Pipe24";
        String pipeName = "TestPipe";
        String pipeURL = "http://test.com";
        com.bezirk.middleware.objects.UhuPipeInfo uhuPipeInfo = new com.bezirk.middleware.objects.UhuPipeInfo(pipeId, pipeName, pipeURL);

        ArrayList<com.bezirk.middleware.objects.UhuPipeInfo> pipeList = new ArrayList<com.bezirk.middleware.objects.UhuPipeInfo>();
        pipeList.add(uhuPipeInfo);

        return pipeList;
    }


    private ArrayList<BezirkDeviceInfo> getDeviceList() {
        String deviceId = "Device123";
        String deviceName = "DeviceA";
        String deviceType = "PC";
        UhuDeviceRole deviceRole = UhuDeviceRole.UHU_MEMBER;
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
