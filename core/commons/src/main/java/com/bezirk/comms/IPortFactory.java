package com.bezirk.comms;

/**
 * Created by vimal on 4/13/2015.
 */
// used for streaming. remove it
public interface IPortFactory {
    public int getPort(String portMapKey);

    public boolean releasePort(int releasingPort);

    public int getNoOfActivePorts();
}
