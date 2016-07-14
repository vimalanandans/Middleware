package com.bezirk.ui.commstest;

/**
 * Sample Ping message that will be sent
 */
public class PingMessage {
    protected String deviceName;
    protected int pingId;
    protected String deviceIp;

    /**
     * @return the deviceName
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * @param deviceName the deviceName to set
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * @return the pingId
     */
    public int getPingId() {
        return pingId;
    }

    /**
     * @param pingId the pingId to set
     */
    public void setPingId(int pingId) {
        this.pingId = pingId;
    }

    /**
     * @return the deviceIp
     */
    public String getDeviceIp() {
        return deviceIp;
    }

    /**
     * @param deviceIp the deviceIp to set
     */
    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }


}
