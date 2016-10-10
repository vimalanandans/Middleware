package com.bezirk.middleware.core.streaming.port;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.streaming.StreamRequest;
import com.bezirk.middleware.core.control.messages.streaming.StreamResponse;
import com.bezirk.middleware.core.streaming.PortFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Port Factory is a Custom Factory class that is instantiated ( singleton ) when the Proxy is
 * instantiated. It reads the values from the properties file and instantiates the starting port and
 * ending port. A call to the StreamPortFactory is made by the ControlReceiverThread when it receives
 * the ControlMessage as STREAM_REQUEST. The port Factory checks if it has any ports free between
 * the Range and gives the Port. It keeps a record in the portsMap in StreamUtilities to avoid
 * reassigning of ports when it receives the same request. StreamPortFactory is also responsible
 * for releasing the ports once the streaming is completed.
 */
public class StreamPortFactory implements PortFactory {
    private static final Logger logger = LoggerFactory.getLogger(StreamPortFactory.class);

    private final int startingPort; // Beginning Port of the RANGE, read from properties file
    private final Set<Integer> activePorts; // Set, that keeps the track of no_of_ports that are active and are been used
    private final com.bezirk.middleware.core.streaming.store.StreamStore streamStore;
    private final int streamMax;
    private int lastAssignedPort; // used to assign the next port when the request comes!

    /**
     * It initializes the beginning and ending ports.
     * It initializes the StreamPortFactory.lastAssignedPort to the statingPort.
     *
     * @param startPort : Beginning port that can be used for Streaming. RANGE (of ports that can be
     *                  used for Streaming) = {@link StreamPortFactory#startingPort - StreamPortFactory#endPort }
     *
     */
    public StreamPortFactory(int startPort, com.bezirk.middleware.core.streaming.store.StreamStore streamStore, int streamMax) {

        startingPort = startPort;
        activePorts = new HashSet<>();
        lastAssignedPort = startPort;
        this.streamStore = streamStore;
        this.streamMax = streamMax;
    }

    /**
     * This method is called by ControlReceiverThread when it receives a {@link ControlMessage} with Discriminator set to "STREAM_REQUEST". This method receives the
     * Key as [ MsgId:ServiceName:DeviceId ] from the {@link StreamRequest} and checks if any ports are available and return the int as positive value if available or -1 if not.
     * If Bezirk has already opened the maximum supported string then it returns -1. If the port is available and the key is not duplicate( i,e the Request has
     * arrived for the first time ) then it updates the activePorts Map with the port and portsMap in StreamStore with portMapKey and port.
     *
     * @param portMapKey key in form [MsgId:ServiceName:DeviceId] that used to keep track of active StreamDescriptor
     * @return positive integer: indicating the port that is assigned for this request. ( Port &gt;= startingPort and  Port&lt;= endingPort-startingPort )
     * -1 : if all the ports are busy.
     */
    @Override
    public int getPort(String portMapKey) {
        synchronized (this) {
            int nextPort = -1;

            if (activePorts.size() == streamMax) {
                logger.debug("MAX STREAMS REACHED");
                return nextPort;
            }

            do {
                nextPort = startingPort + lastAssignedPort % startingPort;
                logger.debug("nextPort: " + nextPort);
                if (activePorts.contains(nextPort)) {

                    lastAssignedPort++;

                } else {

                    activePorts.add(nextPort);
                    if (streamStore.updatePortsMap(portMapKey, nextPort)) {
                        lastAssignedPort = nextPort;
                        break;
                    } else {
                        logger.error("port malfunctioning...");
                        return -1;
                    }

                }
            } while (true);

            logger.debug("New Port assigned: " + nextPort);
            return nextPort;
        }
    }

    /**
     * This method is called by the StreamReceivingThread  after it has received data or if data transfer fails during the transfer.
     * This method removes the record form the portsMap from {@link com.bezirk.middleware.core.streaming.store.StreamStore} and {@link StreamPortFactory#activePorts} from {@link StreamPortFactory}
     *
     * @param releasingPort : The Port that has to be released.
     * @return : <code>true</code> - if the port is released successfully. <code>false</code> - if
     * tried to free the port that does not exist. ( Port Malfunctioning )
     * @see StreamResponse
     */
    @Override
    public boolean releasePort(int releasingPort) {
        synchronized (this) {

            if (activePorts.contains(releasingPort)) {
                logger.debug("No of active ports before releasing: "
                        + activePorts.size());
                activePorts.remove(releasingPort);
                if (!streamStore.releasePort(releasingPort)) {
                    logger.debug("Error updating the ports map to free the ports");
                }
                logger.debug("No of active ports after releasing: "
                        + activePorts.size());
                return true;
            } else {
                logger.error("Port tried to free that doesn't exists..");
                streamStore.releasePort(releasingPort); // Confirmation
                return false;
            }

        }
    }

    /**
     * This method returns the ActivePortsMap that contains all the ports that are being used.
     *
     * @return : size of the Set indicating Number of active ports that are getting streamed at this
     * point of time.
     */
    @Override
    public int getNoOfActivePorts() {
        synchronized (this) {

            return activePorts.size();
        }
    }
}
