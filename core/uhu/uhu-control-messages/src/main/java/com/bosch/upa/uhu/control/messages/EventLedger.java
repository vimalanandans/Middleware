package com.bosch.upa.uhu.control.messages;

/**
 * EventControlMessage is used by Sending and Receiving threads for book-keeping
 * and encryption purposes
 */
public class EventLedger extends Ledger{
    //*** The Following Fields alone are to be In the Control Message
    // Payload has been removed
    // Other Changes are in the Header
    //***
    private String serializedMessage;
    private byte[] encryptedMessage;
    private String serializedHeader;
    private byte[] encryptedHeader;
    private byte[] checksum;
    private byte[] dataOnWire;
	private long lastSent;
    private Integer numOfSends=0;
    private Boolean isLocal = true;
    private Header header;
    //	private Payload payload;
    private Boolean isMulticast = false;
    
    

    public EventLedger(){
        header = new Header();
    }
    /**
     *
     * @return Json(string) which is the serializedMessage
     */
    public String getSerializedMessage() {
        return serializedMessage;
    }

    /**
     *
     * @param serializedMessage The serializedMessage that is returned by invoking UhuMessage.serialize()
     */
    public void setSerializedMessage(String serializedMessage) {
        this.serializedMessage = serializedMessage;
    }

    /**
     *
     * @return The message encrypted by the sphere
     */
    public byte[] getEncryptedMessage() {
        return encryptedMessage==null ?null:encryptedMessage.clone();
    }

    /**
     * @param encryptedMessage The message encrypted by the sphere
     *
     */
    public void setEncryptedMessage(byte[] encryptedMessage) {
        this.encryptedMessage = encryptedMessage==null ?null:encryptedMessage.clone();
    }

    /**
     *
     * @return the encryptedheader
     */
    public byte[] getEncryptedHeader() {
        return encryptedHeader==null ?null:encryptedHeader.clone();
    }

    /**
     *
     * @param encryptedHeader the encryptedHeader
     */
    public void setEncryptedHeader(byte[] encryptedHeader) {
        this.encryptedHeader = encryptedHeader==null ?null:encryptedHeader.clone();
    }
    /**
     * This method is used by the SenderThread to check for time between retransmits.	 *
     * @return the timestamp in milliseconds (as Long) which represents the time at which the message was sent last
     */
    public long getLastSent() {
        return lastSent;
    }

    /**
     *
     * @param lastSent the timestamp in milliseconds which is set every time a message is sent. The lastSent timestamp is overwritten which each send on retransmit
     */
    public void setLastSent(long lastSent) {
        this.lastSent = lastSent;
    }

    /**
     *
     * @return The number of times a message is sent
     */
    public Integer getNumOfSends() {
        return numOfSends;
    }

    /**
     *
     * @param numOfSends This is incremented every time the message is sent
     */
    public void setNumOfSends(Integer numOfSends) {
        this.numOfSends = numOfSends;
    }

  


    public Boolean getIsLocal() {
		return isLocal;
	}
	public void setIsLocal(Boolean isLocal) {
		this.isLocal = isLocal;
	}
	/**
     *
     * @return The header of the message on the wire
     * @see Header
     */
    public Header getHeader() {
        return header;
    }
    /**
     *
     * @param header The header of the message on the wire
     * @see Header
     */
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * 
     * @return the data that is to go on the wire
     */
    public byte[] getDataOnWire() {
		return dataOnWire==null ?null:dataOnWire.clone();
	}
    
    /**
     * 
     * @param dataOnWire this is only set when the message is sent the first time
     */
	public void setDataOnWire(byte[] dataOnWire) {
		this.dataOnWire = dataOnWire==null ?null:dataOnWire.clone();
	}
//	/**
//	 *
//	 * @return the payload that goes on the wire
//	 * @see Payload
//	 */
//	public Payload getPayload() {
//		return payload;
//	}
//	/**
//	 *
//	 * @param payload the payload that goes on the wire
//	 * @see Payload
//	 */
//	public void setPayload(Payload payload) {
//		this.payload = payload;
//	}
    /**
     *
     * @return true is message is a multicast
     */
    public Boolean getIsMulticast() {
        return isMulticast;
    }
    /**
     *
     * @param isMulticast true is message is a multicast
     */
    public void setIsMulticast(Boolean isMulticast) {
        this.isMulticast = isMulticast;
    }
//	/**
//	 * 
//	 * @return true if message is local to the device
//	 */
//	public Boolean getIsLocal() {
//		return isLocal;
//	}
//	/**
//	 * 
//	 * @param isLocal true means the message is local to the device
//	 */
//	public void setIsLocal(Boolean isLocal) {
//		this.isLocal = isLocal;
//	}

    public String getSerializedHeader() {
        return serializedHeader;
    }

    public void setSerializedHeader(String serializedHeader) {
        this.serializedHeader = serializedHeader;
    }
	public byte[] getChecksum() {
		return checksum==null ?null:checksum.clone();
	}
	public void setChecksum(byte[] checksum) {
		this.checksum = checksum==null ?null:checksum.clone();
	}
    
    
}
