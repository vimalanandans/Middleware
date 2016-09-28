package com.bezirk.middleware.core.control.messages.logging;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

public class LoggingServiceMessage extends com.bezirk.middleware.core.control.messages.MulticastControlMessage {
    private final static com.bezirk.middleware.core.control.messages.ControlMessage.Discriminator discriminator = com.bezirk.middleware.core.control.messages.ControlMessage.Discriminator.LOGGING_SERVICE_MESSAGE;

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
