package com.bezirk.middleware.objects;

import com.bezirk.middleware.objects.BezirkDeviceInfo.UhuDeviceRole;

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
        testUhuDeviceInfo();

        testEquality();
    }

    private void testUhuDeviceInfo() {
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

        BezirkDeviceInfo bezirkDeviceInfoTemp = bezirkDeviceInfo;

        assertTrue("Similar uhudevice info are considered unequal.", bezirkDeviceInfo.equals(bezirkDeviceInfoTemp));
        assertEquals("Similar uhudevice info have different hashcode.", bezirkDeviceInfo.hashCode(), bezirkDeviceInfoTemp.hashCode());

        bezirkDeviceInfo = new BezirkDeviceInfo(null, deviceName, deviceType, deviceRole, deviceActive, zirks);
        assertFalse("Different uhudevice info are considered equal.", bezirkDeviceInfoTemp.equals(bezirkDeviceInfo));
        assertFalse("Different uhudevice info are considered equal.", bezirkDeviceInfo.equals(bezirkDeviceInfoTemp));

        assertFalse("BezirkZirkInfo is considered equal to uhudeviceinfo.", bezirkDeviceInfo.equals(bezirkZirkInfo));
        assertNotEquals("Different uhudeviceinfo  have same hashcode.", bezirkDeviceInfo.hashCode(), bezirkDeviceInfoTemp.hashCode());

        bezirkDeviceInfoTemp = new BezirkDeviceInfo("DeviceB", deviceName, deviceType, deviceRole, deviceActive, zirks);
        assertFalse("Different uhudevice info are considered equal.", bezirkDeviceInfoTemp.equals(bezirkDeviceInfo));
        assertFalse("Different uhudevice info are considered equal.", bezirkDeviceInfo.equals(bezirkDeviceInfoTemp));


    }

}
