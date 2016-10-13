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
package com.bezirk.middleware.core.control.messages.logging;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

public class LoggingServiceMessage extends com.bezirk.middleware.core.control.messages.MulticastControlMessage {
    private static final ControlMessage.Discriminator discriminator =
            ControlMessage.Discriminator.LOGGING_SERVICE_MESSAGE;

    protected String remoteLoggingServiceIP;
    protected int remoteLoggingServicePort;
    protected String[] sphereList;
    protected boolean loggingStatus;

    public LoggingServiceMessage() {
    }

    public LoggingServiceMessage(BezirkZirkEndPoint sender, String sphereId, String serverIp, int serverPort,
                                 String[] sphereList, boolean loggingStatus) {
        super(sender, sphereId, discriminator);
        this.remoteLoggingServiceIP = serverIp;
        this.remoteLoggingServicePort = serverPort;
        this.sphereList = sphereList == null ? null : sphereList.clone();
        this.loggingStatus = loggingStatus;
    }

    public String getRemoteLoggingServiceIP() {
        return remoteLoggingServiceIP;
    }

    public void setRemoteLoggingServiceIP(String remoteLoggingServiceIP) {
        this.remoteLoggingServiceIP = remoteLoggingServiceIP;
    }

    public int getRemoteLoggingServicePort() {
        return remoteLoggingServicePort;
    }

    public void setRemoteLoggingServicePort(int remoteLoggingServicePort) {
        this.remoteLoggingServicePort = remoteLoggingServicePort;
    }

    public String[] getSphereList() {
        return sphereList == null ? null : sphereList.clone();
    }

    public void setSphereList(String[] sphereList) {
        this.sphereList = sphereList == null ? null : sphereList.clone();
    }

    public boolean isLoggingStatus() {
        return loggingStatus;
    }

    public void setLoggingStatus(boolean loggingStatus) {
        this.loggingStatus = loggingStatus;
    }

}
