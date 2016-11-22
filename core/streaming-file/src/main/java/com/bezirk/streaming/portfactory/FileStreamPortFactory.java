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
package com.bezirk.streaming.portfactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by PIK6KOR on 11/14/2016.
 */

public class FileStreamPortFactory{

    private final int startingPort;
    private final Set<Integer> activePorts;
    private final int streamMax;
    private int lastAssignedPort; // used to assign the next port when the request comes!

    //holds the value for port number assigned for a stream id.
    private final Map<Short, Integer> portsMap = new HashMap<Short, Integer>();


    //move this to the property file, this should be configurable.
    static Short STREAM_START_PORT = 6321;
    static Short STREAM_PARALLEL_MAX = 10;

    //default constructor
    public FileStreamPortFactory() {
        startingPort = STREAM_START_PORT;
        activePorts = new HashSet<Integer>();
        lastAssignedPort = STREAM_START_PORT;
        this.streamMax = STREAM_PARALLEL_MAX;
    }


    /**
     *
     * @param portMapKey
     * @return
     */
    public Integer getActivePort(Short portMapKey) {
        synchronized (this) {
            int nextPort = -1;

            if (activePorts.size() == streamMax) {
                return -1;
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

    private boolean updatePortsMap(Short portMapKey, int value) {

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

    /**
     *
     * @return
     */
    private Map<Short, Integer> getPortsMap(){
        synchronized (this) {
            return portsMap;
        }
    }

    public boolean releasePort(int port) {
        synchronized (this) {
            boolean updatedPortMap = true;

            if (port <= 0) {
                return false;
            }

            if (getPortsMap().containsValue(port)) {

                Iterator<Map.Entry<Short, Integer>> portsMapIterator = getPortsMap()
                        .entrySet().iterator();
                while (portsMapIterator.hasNext()) {
                    Map.Entry<Short, Integer> entry = portsMapIterator.next();
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

    public int getNoOfActivePorts() {
        synchronized (this) {
            return activePorts.size();
        }
    }
}
