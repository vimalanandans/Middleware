package com.bezirk.control.messages;

/**
 * Event Ledger to hold event message and header
 * */
public class EventLedger implements Ledger {
    private String serializedMessage;
    private byte[] encryptedMessage;
    private String serializedHeader;
    private byte[] encryptedHeader;
    private Header header;
    private Boolean isMulticast = false;


    public EventLedger() {
      //  header = new Header();
    }

    /**
     * @return Json(string) which is the serializedMessage
     */
    public String getSerializedMessage() {
        return serializedMessage;
    }

    /**
     * @param serializedMessage The serializedMessage that is returned by invoking Message.toJson()
     */
    public void setSerializedMessage(String serializedMessage) {
        this.serializedMessage = serializedMessage;
    }

    /**
     * @return The message encrypted by the sphere
     */
    public byte[] getEncryptedMessage() {
        return encryptedMessage == null ? null : encryptedMessage.clone();
    }

    /**
     * @param encryptedMessage The message encrypted by the sphere
     */
    public void setEncryptedMessage(byte[] encryptedMessage) {
        this.encryptedMessage = encryptedMessage == null ? null : encryptedMessage.clone();
    }

    /**
     * @return the encryptedHeader
     */
    public byte[] getEncryptedHeader() {
        return encryptedHeader == null ? null : encryptedHeader.clone();
    }

    /**
     * @param encryptedHeader the encryptedHeader
     */
    public void setEncryptedHeader(byte[] encryptedHeader) {
        this.encryptedHeader = encryptedHeader == null ? null : encryptedHeader.clone();
    }


    /**
     * @return The header of the message on the wire
     * @see com.bezirk.control.messages.Header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * @param header The header of the message on the wire
     * @see com.bezirk.control.messages.Header
     */
    public void setHeader(Header header) {
        this.header = header;
    }


    /**
     * @return true is message is a multicast
     */
    public Boolean getIsMulticast() {
        return isMulticast;
    }

    /**
     * @param isMulticast true is message is a multicast
     */
    public void setIsMulticast(Boolean isMulticast) {
        this.isMulticast = isMulticast;
    }


    public String getSerializedHeader() {
        return serializedHeader;
    }

    public void setSerializedHeader(String serializedHeader) {
        this.serializedHeader = serializedHeader;
    }



}
