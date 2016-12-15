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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  Port factory will handle the ports in given range. Here we can retreive active port
 *  release the unused port, These ports map will have portKey {@link com.bezirk.streaming.StreamRecord#streamId}
 *  and port as value.
 */

public class FileStreamPortFactory{

    private final int startingPort;
    private final Set<Integer> activePorts;
    private final int streamMax;
    private int lastAssignedPort; // used to assign the next port when the request comes!

    //holds the value for port number assigned for a stream id.
    private final Map<Short, Integer> portsMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(FileStreamPortFactory.class);

    //default constructor
    public FileStreamPortFactory() {
        Short streamStartPort = 6321;
        startingPort = streamStartPort;
        activePorts = new HashSet<>();
        lastAssignedPort = streamStartPort;
        this.streamMax = 10;
    }


    /**
     * retrieve a active port from the given port range
     * @param portMapKey portmap key
     * @return a free active port, available for open connection.
     */
    public Integer getActivePort(Short portMapKey) {
        synchronized (this) {
            int nextPort;

            if (activePorts.size() == streamMax) {
                logger.error("all ports are consumed for streaming");
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
            logger.info("assigned port for streaming {}", nextPort);
            return nextPort;
        }
    }

    /**
     * update the portMap when the port is released or when free.
     * @param portMapKey portmap key is the port key
     * @param value port calue
     * @return boolean value based on the update feature
     */
    private boolean updatePortsMap(Short portMapKey, int value) {

        synchronized (this) {

            if (value <= 0) {
                logger.error("empty values for either key or value");
                return false;
            }

            if (portsMap.containsKey(portMapKey)) {
                logger.error("port key already exists..");
                return false;
            }
            getPortsMap().put(portMapKey, value);
            logger.debug("portsmap updated with key : value: key: {}  value: {}",portMapKey, value);
            return true;

        }
    }

    /**
     * returns the complete port map
     * @return complete port map
     */
    private Map<Short, Integer> getPortsMap(){
        synchronized (this) {
            return portsMap;
        }
    }

    /**
     * release a port which is free to be consumed
     * @param port port to be released
     * @return if the process was successful return true else false
     */
    public boolean releasePort(int port) {
        synchronized (this) {
            boolean updatedPortMap = true;

            if (port <= 0) {
                logger.debug("error in releasing port from portfactory {}", port);
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
                logger.debug("releasing port from port factory {}", port);
            } else {
                updatedPortMap = false;
                logger.debug("port map was not updated from port factory {} for ", port);
            }

            return updatedPortMap;
        }

    }

}
