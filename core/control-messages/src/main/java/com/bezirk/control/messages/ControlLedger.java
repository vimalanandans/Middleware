package com.bezirk.control.messages;


/**
 * Control Ledger holds the control message related info for the internal data set
 *
 */
public class ControlLedger extends Ledger {
    private ControlMessage message;
    private String serializedMessage;   // used on receiving end only
    private byte[] encryptedMessage;
    //private byte[] sendData;          // unused currently was used in UDP
    //private long lastSent;              // unused currently was used in UDP
    private Boolean isMessageFromHost = true; // usage not clear
    //private Integer numOfSends = 0;
    private String sphereId;    // Fixme Control Message is already having sphere id.
    //private byte[] checksum; // unused currently was used in UDP
    private byte[] dataOnWire;

    public ControlMessage getMessage() {
        return message;
    }

    public void setMessage(ControlMessage message) {
        this.message = message;
    }

    public String getSerializedMessage() {
        return serializedMessage;
    }

    public void setSerializedMessage(String serializedMessage) {
        this.serializedMessage = serializedMessage;
    }

    public byte[] getEncryptedMessage() {
        return encryptedMessage == null ? null : encryptedMessage.clone();
    }

    public void setEncryptedMessage(byte[] encryptedMessage) {
        this.encryptedMessage = encryptedMessage == null ? null : encryptedMessage.clone();
    }

//    public long getLastSent() {
//        return lastSent;
//    }
//
//    public void setLastSent(long lastSent) {
//        this.lastSent = lastSent;
//    }

//    public Integer getNumOfSends() {
//        return numOfSends;
//    }
//
//    public void setNumOfSends(Integer numOfSends) {
//        this.numOfSends = numOfSends;
//    }

    public String getSphereId() {
        return sphereId;
    }

    public void setSphereId(String sphereName) {
        this.sphereId = sphereName;
    }

    @Deprecated //usage is not clear
    public Boolean getIsMessageFromHost() {
        return isMessageFromHost;
    }

    @Deprecated //usage is not clear
    public void setIsMessageFromHost(Boolean isMessageFromHost) {
        this.isMessageFromHost = isMessageFromHost;
    }
//
//    public byte[] getSendData() {
//        return sendData == null ? null : sendData.clone();
//    }
//
//    public void setSendData(byte[] sendData) {
//        this.sendData = sendData == null ? null : sendData.clone();
//    }
//
//    public byte[] getChecksum() {
//        return checksum == null ? null : checksum.clone();
//    }
//
//    public void setChecksum(byte[] checksum) {
//        this.checksum = checksum == null ? null : checksum.clone();
//    }
//
//    public byte[] getDataOnWire() {
//        return dataOnWire == null ? null : dataOnWire.clone();
//    }
//
//    public void setDataOnWire(byte[] dataOnWire) {
//        this.dataOnWire = dataOnWire == null ? null : dataOnWire.clone();
//    }

}
