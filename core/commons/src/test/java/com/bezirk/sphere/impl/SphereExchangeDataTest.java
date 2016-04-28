package com.bezirk.sphere.impl;

import com.bezirk.device.BezirkDeviceType;
import com.bezirk.sphere.api.BezirkSphereType;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ajc6kor
 */
public class SphereExchangeDataTest {

    @Test
    public void test() {

        String deviceId = "Device123";
        String deviceName = "DeviceA";
        String deviceType = BezirkDeviceType.BEZIRK_DEVICE_TYPE_CAR;
        byte[] spherePublicKeyBytes = "spherepublicKey".getBytes();
        String sphereId = "TestSphere123";
        byte[] sphereKeyBytes = "sphereKey".getBytes();
        String sphereName = "TestSphere";
        String sphereType = BezirkSphereType.BEZIRK_SPHERE_TYPE_CAR;

        SphereExchangeData sphereExchangeData = new SphereExchangeData();
        sphereExchangeData.setDeviceID(deviceId);
        sphereExchangeData.setDeviceName(deviceName);
        sphereExchangeData.setDeviceType(deviceType);
        sphereExchangeData.setOwnerPublicKeyBytes(spherePublicKeyBytes);
        sphereExchangeData.setSphereID(sphereId);
        sphereExchangeData.setSphereKey(sphereKeyBytes);
        sphereExchangeData.setSphereName(sphereName);
        sphereExchangeData.setSphereType(sphereType);

        String serializedSphereExchangeData = sphereExchangeData.serialize();

        SphereExchangeData deserializedSphereExchangeData = SphereExchangeData.deserialize(serializedSphereExchangeData);

        assertEquals("DeviceId is not equal to the set value.", deviceId, deserializedSphereExchangeData.getDeviceID());
        assertEquals("DeviceName is not equal to the set value.", deviceName, deserializedSphereExchangeData.getDeviceName());
        assertEquals("DeviceType is not equal to the set value.", deviceType, deserializedSphereExchangeData.getDeviceType());
        assertArrayEquals("OwnerPublicKeyBytes is not equal to the set value.", spherePublicKeyBytes, deserializedSphereExchangeData.getOwnerPublicKeyBytes());
        assertEquals("SphereId is not equal to the set value.", sphereId, deserializedSphereExchangeData.getSphereID());
        assertArrayEquals("SphereKey is not equal to the set value.", sphereKeyBytes, deserializedSphereExchangeData.getSphereKey());
        assertEquals("SphereName is not equal to the set value.", sphereName, deserializedSphereExchangeData.getSphereName());
        assertEquals("SphereType is not equal to the set value.", sphereType, deserializedSphereExchangeData.getSphereType());

        assertTrue("Spherekeys are not considered.", sphereExchangeData.isKeysExist());


    }

}
