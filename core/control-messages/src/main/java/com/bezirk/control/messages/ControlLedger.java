package com.bezirk.control.messages;


/**
 * Control Ledger holds the control message related info for the internal data set
 *
 */
public class ControlLedger implements Ledger {
    private ControlMessage message;
    private String serializedMessage;   // used on receiving end only
    private byte[] encryptedMessage;
    private Boolean isMessageFromHost = true; // usage not clear
    private String sphereId;    // Fixme Control Message is already having sphere id.

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


}
