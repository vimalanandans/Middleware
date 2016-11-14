package com.bezirk.streaming.portfactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamPortFactory implements PortFactory {

    private final int startingPort; // Beginning Port of the RANGE, read from properties file
    private final Set<Integer> activePorts; // Set, that keeps the track of no_of_ports that are active and are been used
    private final int streamMax;
    private int lastAssignedPort; // used to assign the next port when the request comes!

    //holds the value for port number assigned for a stream id.
    private final Map<String, Integer> portsMap = new HashMap<String, Integer>();

    /**
     *
     * @param startPort
     * @param streamMax
     */
    public FileStreamPortFactory(int startPort, int streamMax) {

        startingPort = startPort;
        activePorts = new HashSet<Integer>();
        lastAssignedPort = startPort;
        this.streamMax = streamMax;
    }


    @Override
    public int getActivePort(String portMapKey) {
        synchronized (this) {
            int nextPort = -1;

            if (activePorts.size() == streamMax) {
                return nextPort;
            }

            do {
                nextPort = startingPort + lastAssignedPort % startingPort;
                if (activePorts.contains(nextPort)) {

                    lastAssignedPort++;

                } else {

                    activePorts.add(nextPort);
                    if (updatePortsMap(portMapKey, nextPort)) {
                        lastAssignedPort = nextPort;
                        break;
                    } else {
                        return -1;
                    }

                }
            } while (true);

            return nextPort;
        }
    }

    private boolean updatePortsMap(String portMapKey, int value) {

        synchronized (this) {

            if (value <= 0) {
                //logger.error("empty values for either key or value");
                return false;
            }

            if (portsMap.containsKey(portMapKey)) {
                //logger.error("port key already exists..");
                return false;
            }
            getPortsMap().put(portMapKey, value);
            /*logger.debug("portsmap updated with key : value:" + "key:" + portMapKey
                    + " value:" + value);*/
            return true;

        }
    }

    /*
     * This method returns the {@link this#portsMap}, that maps
     * <[MsagId:ServiceName:DeviceId],Port>
     *
     * @return the PortsMap<Integer, StreamRecord>
     */
    private Map<String, Integer> getPortsMap(){
        synchronized (this) {
            return portsMap;
        }
    }

    @Override
    public boolean releasePort(int port) {
        synchronized (this) {
            boolean updatedPortMap = true;

            if (port <= 0) {
                return false;
            }

            if (getPortsMap().containsValue(port)) {

                Iterator<Map.Entry<String, Integer>> portsMapIterator = getPortsMap()
                        .entrySet().iterator();
                while (portsMapIterator.hasNext()) {
                    Map.Entry<String, Integer> entry = portsMapIterator.next();
                    if (entry.getValue() == port) {
                        portsMapIterator.remove();
                        break;
                    }
                }
            } else {
                updatedPortMap = false;
            }
            return updatedPortMap;
        }

    }

    @Override
    public int getNoOfActivePorts() {
        synchronized (this) {
            return activePorts.size();
        }
    }
}
