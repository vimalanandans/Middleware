package com.bosch.upa.uhu.commstest.ui;

/**
 * Sample Pong message that will be sent
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
public class PongMessage {
    protected String deviceName;
    protected int pingId;
    protected String senderIP;
    protected String pingRequestId;
    
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
     * @return the senderIP
     */
    public String getSenderIP() {
        return senderIP;
    }
    /**
     * @param senderIP the senderIP to set
     */
    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }
    /**
     * @return the pingRequestId
     */
    public String getPingRequestId() {
        return pingRequestId;
    }
    /**
     * @param pingRequestId the pingRequestId to set
     */
    public void setPingRequestId(String pingRequestId) {
        this.pingRequestId = pingRequestId;
    }
}
