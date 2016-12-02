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
 * Control Ledger holds the control message related info for the internal data set
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
