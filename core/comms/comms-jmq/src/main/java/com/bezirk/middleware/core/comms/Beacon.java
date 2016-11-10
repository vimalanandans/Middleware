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
package com.bezirk.middleware.core.comms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZBeacon;
import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;

class Beacon {
    private static final Logger logger = LoggerFactory.getLogger(Beacon.class);
    private static final int MSG_FIELDS = 4;
    private static final String SEPARATOR = "::";
    private static final String beaconHost = "255.255.255.255";
    private static final int beaconPort = 5670;
    private ZBeacon zbeacon;
    private final String groupName;
    private final int port;
    private final UUID myId;
    private final BeaconCallback beaconCallback;

    interface BeaconCallback {
        void processPeer(UUID uuid, InetAddress senderInetAddress, int port);
    }

    Beacon(@NotNull final String groupName, final int port, @NotNull final UUID myId,
           @NotNull final BeaconCallback callback) {
        this.groupName = groupName;
        this.port = port;
        this.myId = myId;
        this.beaconCallback = callback;
        logger.info("GroupName for current bezirk instance {}", this.groupName);
        logger.trace("Port being broadcast in beacon {}", port);
    }

    private void processBeacon(@NotNull final InetAddress sender, @NotNull final byte[] beacon) {
        final String beaconString = new String(beacon, ZMQ.CHARSET);
        final String[] data;
        try {
            data = beaconString.split(SEPARATOR);
        } catch (PatternSyntaxException e) {
            logger.debug("Failed to split beacon string", e);
            return;
        }

        if (data.length == MSG_FIELDS) {
            try {
                final long lsb = Long.parseLong(data[1]);
                final long msb = Long.parseLong(data[2]);
                final UUID uuid = new UUID(msb, lsb);
                final int senderPort = Integer.parseInt(data[3]);
                logger.trace("uuid {}, port {}, sender {}", uuid, senderPort, sender);
                beaconCallback.processPeer(uuid, sender, senderPort);
            } catch (NumberFormatException e) {
                logger.warn("NumberFormatException while processing beacon", e);
            }
        }
    }

    public void start() {
        final String beaconData = groupName + SEPARATOR + myId.getLeastSignificantBits() + SEPARATOR
                + myId.getMostSignificantBits() + SEPARATOR + String.valueOf(port);
        final byte[] beaconDataBytes = beaconData.getBytes(ZMQ.CHARSET);

        zbeacon = new ZBeacon(beaconHost, beaconPort, beaconDataBytes, false);
        // this ensures only beacon with this prefix is processed
        zbeacon.setPrefix(groupName.getBytes(ZMQ.CHARSET));

        zbeacon.setUncaughtExceptionHandlers(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.warn("ClientHandler: Unable to beacon. This generally happens due to loss of network connectivity. " +
                        "When network connectivity is resumed, existing nodes can communicate again. " +
                        "Communication between existing & new nodes would require Bezirk to be restarted.", e);
            }
        }, new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.warn("ServerHandler: Unable to beacon. This generally happens due to loss of network connectivity. " +
                        "When network connectivity is resumed, existing nodes can communicate again. " +
                        "Communication between existing & new nodes would require Bezirk to be restarted.", e);
            }
        });

        zbeacon.setListener(new ZBeacon.Listener() {
            @Override
            public void onBeacon(InetAddress sender, byte[] beacon) {
                if (!Arrays.equals(beacon, beaconDataBytes)) {
                    processBeacon(sender, beacon);
                } else {
                    // ignore beacon from the same node
                    logger.trace("beacon from self node, sender {} data {}", sender, Arrays.toString(beacon));
                }
            }
        });

        zbeacon.start();
    }

    public void stop() {
        try {
            if (zbeacon != null) {
                zbeacon.stop();
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException while shutting down beacon", e);
            Thread.currentThread().interrupt();
        }
    }
}
