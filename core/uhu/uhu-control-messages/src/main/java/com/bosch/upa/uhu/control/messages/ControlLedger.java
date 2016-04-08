package com.bosch.upa.uhu.control.messages;



/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 * There are two running instances of type ControlQueue. 
 * ControlSenderQueue: is the queue for control messages on the sender side which is populated by services using the UhuProxy and processed by the ControlSenderThread
 * ControlReceiverQueue: is the queue for control messages on the receiver side which is populated by the ControlMulticastListener and ControlUnicastListener. The queue is processed by the ControlReceiverThread
 */
public class ControlLedger extends Ledger {
    private ControlMessage message;
    private String serializedMessage; // used on receiving end only
    private byte[] encryptedMessage;
    private byte[] sendData;
    private long lastSent;
    private Boolean isMessageFromHost = true;
    private Integer numOfSends=0;
    private String sphereId;
    private byte[] checksum;
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
        return encryptedMessage==null ?null:encryptedMessage.clone();
    }

    public void setEncryptedMessage(byte[] encryptedMessage) {
        this.encryptedMessage = encryptedMessage==null ?null:encryptedMessage.clone();
    }

    public long getLastSent() {
        return lastSent;
    }

    public void setLastSent(long lastSent) {
        this.lastSent = lastSent;
    }

    public Integer getNumOfSends() {
        return numOfSends;
    }

    public void setNumOfSends(Integer numOfSends) {
        this.numOfSends = numOfSends;
    }

    public String getSphereId() {
        return sphereId;
    }

    public void setSphereId(String sphereName) {
        this.sphereId = sphereName;
    }

    public Boolean getIsMessageFromHost() {
        return isMessageFromHost;
    }

    public void setIsMessageFromHost(Boolean isMessageFromHost) {
        this.isMessageFromHost = isMessageFromHost;
    }

	public byte[] getSendData() {
		return sendData==null ?null:sendData.clone();
	}

	public void setSendData(byte[] sendData) {
		this.sendData = sendData==null ?null:sendData.clone();
	}
	
	public byte[] getChecksum() {
		return checksum==null ?null:checksum.clone();
	}

	public void setChecksum(byte[] checksum) {
		this.checksum = checksum==null ?null:checksum.clone();
	}

	public byte[] getDataOnWire() {
		return dataOnWire==null ?null:dataOnWire.clone();
	}

	public void setDataOnWire(byte[] dataOnWire) {
		this.dataOnWire = dataOnWire==null ?null:dataOnWire.clone();
	}
   
}
