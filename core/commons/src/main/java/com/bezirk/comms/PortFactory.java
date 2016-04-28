package com.bezirk.comms;

/**
 * @author Vimal
 */
// used for streaming. remove it
public interface PortFactory {
    public int getPort(String portMapKey);

    public boolean releasePort(int releasingPort);

    public int getNoOfActivePorts();
}
