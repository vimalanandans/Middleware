/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.control.messages;

/**
 * Event Ledger to hold event message and header
 */
public class EventLedger implements Ledger {
    private String serializedMessage;
    private byte[] encryptedMessage;
    private String serializedHeader;
    private byte[] encryptedHeader;
    private Header header;
    private Boolean isMulticast = false;


    public EventLedger() {
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
     * @see Header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * @param header The header of the message on the wire
     * @see Header
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
