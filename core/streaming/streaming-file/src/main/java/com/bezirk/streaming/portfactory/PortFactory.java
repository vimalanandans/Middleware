package com.bezirk.streaming.portfactory;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public interface PortFactory {

    int getActivePort(String portMapKey);

    boolean releasePort(int releasingPort);

    int getNoOfActivePorts();
}
