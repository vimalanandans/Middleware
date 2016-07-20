package com.bezirk.sphere.impl;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.apache.shiro.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SphereExchangeData {
    private static final Logger logger = LoggerFactory.getLogger(SphereExchangeData.class);

    public static final String OPERATION_SHARE = "Share";
    public static final String OPERATION_CATCH = "Catch";

    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String sphereId;
    private String sphereName;
    private String sphereType;
    private String sphereKey = null;
    private String spherePublicKey = null;

    public SphereExchangeData() {
    }

    /**
     * De-toJson the string to object
     *
     * @param data
     * @return
     */
    public static SphereExchangeData deserialize(String data) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(data, SphereExchangeData.class);
        } catch (JsonParseException e) {
            logger.error("Deserialization error", e);
        }
        return null;
    }

    /**
     * Get the JSON string of the whole object
     *
     * @return
     */
    public String serialize() {
        Gson gson = new Gson();
        String data = gson.toJson(this);

        logger.debug("toJson data > {}", data);

        return data;
    }

    public String getSphereName() {
        return sphereName;
    }

    public void setSphereName(String sphereName) {
        this.sphereName = sphereName;
    }

    public String getSphereID() {
        return sphereId;
    }

    public void setSphereID(String sphereId) {
        this.sphereId = sphereId;
    }

    public boolean isKeysExist() {
        return (sphereKey != null) && spherePublicKey != null;
    }

    public byte[] getSphereKey() {
        return Base64.decode(sphereKey);
    }

    public void setSphereKey(byte[] sphereKeyBytes) {

        this.sphereKey = Base64.encodeToString(sphereKeyBytes);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceID() {
        return deviceId;
    }

    public void setDeviceID(String deviceId) {
        this.deviceId = deviceId;
    }

    // return always the byte stream
    public byte[] getOwnerPublicKeyBytes() {
        return Base64.decode(spherePublicKey);
    }

    public void setOwnerPublicKeyBytes(byte[] spherePublicKeyBytes) {
        this.spherePublicKey = Base64.encodeToString(spherePublicKeyBytes);
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;

    }

    public String getSphereType() {
        return sphereType;
    }

    public void setSphereType(String sphereType) {
        this.sphereType = sphereType;

    }
}