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
 *  release the unused port, These ports map will have portKey
 *  {@link com.bezirk.streaming.StreamRecord#streamId} and port as value.
 */

public class FileStreamPortFactory{

    private static final int STARTING_PORT = 6321;
    private static final Set<Integer> ACTIVE_PORTS = new HashSet<>();
    private static final int MAX_STREAM_COUNT = 10;
    private static int lastAssignedPort = STARTING_PORT;

    //holds the value for port number assigned for a stream id.
    private final Map<Long, Integer> portsMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(FileStreamPortFactory.class);

    /**
     * retrieve a active port from the given port range
     * @param portMapKey portmap key
     * @return a free active port, available for open connection.
     */
    public int getAvailablePort(Long portMapKey) {
        synchronized (this) {
            int nextPort;

            if (ACTIVE_PORTS.size() == MAX_STREAM_COUNT) {
                logger.error("all ports are consumed for streaming");
                return -1;
            }

            do {
                nextPort = STARTING_PORT + lastAssignedPort % STARTING_PORT;
                if (ACTIVE_PORTS.contains(nextPort)) {
                    lastAssignedPort++;
                } else {
                    ACTIVE_PORTS.add(nextPort);
                    if (updatePortsMap(portMapKey, nextPort)) {
                        lastAssignedPort = nextPort;
                        break;
                    } else {
                        return -1;
                    }

                }
            } while (true);
            logger.debug("assigned port {} for streaming id {}", nextPort, portMapKey);
            return nextPort;
        }
    }

    /**
     * update the portMap when the port is released or when free.
     * @param portMapKey portmap key is the port key
     * @param value port value
     * @return boolean value based on the update feature
     */
    private boolean updatePortsMap(Long portMapKey, int value) {

        synchronized (this) {

            if (portMapKey <=0 || value <= 0) {
                logger.error("empty values for either key or value");
                return false;
            }

            if (portsMap.containsKey(portMapKey)) {
                logger.error("port key already exists..");
                return false;
            }
            getPortsMap().put(portMapKey, value);
            logger.debug("portmap was updated with key : value: key: {}  value: {}",
                    portMapKey, value);
            return true;

        }
    }

    /**
     * returns the complete port map
     * @return complete port map
     */
    private Map<Long, Integer> getPortsMap(){
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
                logger.debug("Cannot release invalid port number {}", port);
                return false;
            }

            if (getPortsMap().containsValue(port)) {

                final Iterator<Integer> portsMapIterator = getPortsMap().values().iterator();
                while (portsMapIterator.hasNext()) {
                    final int portValueEntry = portsMapIterator.next();
                    if (portValueEntry == port) {
                        portsMapIterator.remove();
                        break;
                    }
                }
                logger.debug("releasing port {} in port factory", port);
            } else {
                updatedPortMap = false;
                logger.debug("port map was not updated in port factory for port {}", port);
            }

            return updatedPortMap;
        }

    }

}
