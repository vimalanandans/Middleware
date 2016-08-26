package com.bezirk.middleware.core.streaming;

/**
 * @author Vimal
 */
// used for streaming. remove it
public interface PortFactory {
    int getPort(String portMapKey);

    boolean releasePort(int releasingPort);

    int getNoOfActivePorts();
}
