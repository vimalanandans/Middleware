package com.bezirk.middleware.core.objects;

import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo.BezirkDeviceRole;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the BezirkDeviceInfo by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */
public class BezirkDeviceInfoTest {
    @Test
    public void test() {
        testBezirkDeviceInfo();

        testEquality();
    }

    private void testBezirkDeviceInfo() {
        String deviceId = "Device123";
        String deviceName = "DeviceA";
        String deviceType = "PC";
        BezirkDeviceRole deviceRole = BezirkDeviceInfo.BezirkDeviceRole.BEZIRK_MEMBER;
        boolean deviceActive = true;
        String zirkName = "ZirkA";
        String zirkId = "Zirk123";
        String zirkType = "MemberZirk";
        boolean active = true;
        boolean visible = true;
        com.bezirk.middleware.objects.BezirkZirkInfo bezirkZirkInfo = new com.bezirk.middleware.objects.BezirkZirkInfo(zirkId, zirkName, zirkType, active, visible);
        List<com.bezirk.middleware.objects.BezirkZirkInfo> zirks = new ArrayList<>();
        zirks.add(bezirkZirkInfo);

        BezirkDeviceInfo bezirkDeviceInfo = new BezirkDeviceInfo(deviceId, deviceName,
                deviceType, deviceRole, deviceActive, zirks);


        assertEquals("DeviceID is not equal to the set value.", deviceId, bezirkDeviceInfo.getDeviceId());
        assertEquals("DeviceName is not equal to the set value.", deviceName, bezirkDeviceInfo.getDeviceName());
        assertEquals("DeviceRole is not equal to the set value.", deviceRole, bezirkDeviceInfo.getDeviceRole());
        assertEquals("DeviceType is not equal to the set value.", deviceType, bezirkDeviceInfo.getDeviceType());
        assertEquals("ZirkList is not equal to the set value.", zirks, bezirkDeviceInfo.getZirkList());
        assertTrue("Device is not considered active", bezirkDeviceInfo.isDeviceActive());

        deviceName = "DeviceB";
        BezirkDeviceInfo bezirkDeviceInfoTemp = new BezirkDeviceInfo(deviceId, deviceName,
                deviceType, deviceRole, deviceActive, zirks);
        assertFalse("Different bezirkDeviceInfo has same string representation.", bezirkDeviceInfo.toString().equalsIgnoreCase(bezirkDeviceInfoTemp.toString()));
    }

    private void testEquality() {
        String deviceId = "Device123";
        String deviceName = "DeviceA";
        String deviceType = "PC";
        BezirkDeviceRole deviceRole = BezirkDeviceInfo.BezirkDeviceRole.BEZIRK_MEMBER;
        boolean deviceActive = true;
        String zirkName = "ZirkA";
        String zirkId = "Zirk123";
        String zirkType = "MemberZirk";
        boolean active = true;
        boolean visible = true;
        com.bezirk.middleware.objects.BezirkZirkInfo bezirkZirkInfo = new com.bezirk.middleware.objects.BezirkZirkInfo(zirkId, zirkName, zirkType, active, visible);
        List<com.bezirk.middleware.objects.BezirkZirkInfo> zirks = new ArrayList<>();
        zirks.add(bezirkZirkInfo);

        BezirkDeviceInfo bezirkDeviceInfo = new BezirkDeviceInfo(deviceId, deviceName,
                deviceType, deviceRole, deviceActive, zirks);

        BezirkDeviceInfo bezirkDeviceInfoTemp = bezirkDeviceInfo;

        assertTrue("Similar bezirkdevice info are considered unequal.", bezirkDeviceInfo.equals(bezirkDeviceInfoTemp));
        assertEquals("Similar bezirkdevice info have different hashcode.", bezirkDeviceInfo.hashCode(), bezirkDeviceInfoTemp.hashCode());

        bezirkDeviceInfo = new BezirkDeviceInfo(null, deviceName, deviceType, deviceRole, deviceActive, zirks);
        assertFalse("Different bezirkdevice info are considered equal.", bezirkDeviceInfoTemp.equals(bezirkDeviceInfo));
        assertFalse("Different bezirkdevice info are considered equal.", bezirkDeviceInfo.equals(bezirkDeviceInfoTemp));

        assertFalse("BezirkZirkInfo is considered equal to bezirkdeviceinfo.", bezirkDeviceInfo.equals(bezirkZirkInfo));
        assertNotEquals("Different bezirkdevice  have same hashcode.", bezirkDeviceInfo.hashCode(), bezirkDeviceInfoTemp.hashCode());

        bezirkDeviceInfoTemp = new BezirkDeviceInfo("DeviceB", deviceName, deviceType, deviceRole, deviceActive, zirks);
        assertFalse("Different bezirkdevice info are considered equal.", bezirkDeviceInfoTemp.equals(bezirkDeviceInfo));
        assertFalse("Different bezirkdevice info are considered equal.", bezirkDeviceInfo.equals(bezirkDeviceInfoTemp));


    }

}
