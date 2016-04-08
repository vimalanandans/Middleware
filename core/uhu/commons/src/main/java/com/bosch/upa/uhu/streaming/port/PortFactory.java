/**
 * @author: Vijet Badigannavar ( bvijet@in.bosch.com )
 */
package com.bosch.upa.uhu.streaming.port;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.comms.IPortFactory;
import com.bosch.upa.uhu.comms.UhuComms;
import com.bosch.upa.uhu.streaming.store.StreamStore;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.streaming.StreamRequest;

/**
 *  Port Factory is a Custom Factory class that is instantiated ( singleton ) when the Proxy is instantiated. It reads the values from the
 *  properties file and instantiates the starting port and ending port. A call to the PortFactory is made by the ControlReceiverThread when
 *  it receives the ControlMessage as StreamRequest. The port Factory checks if it has any ports free between the Range and gives the Port. It
 *  keeps a record in the portsMap in StreamUtilities to avoid reassigning of ports when it receives the same request. PortFactory is also responsible
 *  for releasing the ports once the streaming is completed.
 *  
 * @see com.bosch.upa.uhu.comms.UhuComms
 */
public class PortFactory implements IPortFactory {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PortFactory.class);

    private final int startingPort; // Beginning Port of the RANGE, read from properties file
    private final Set<Integer> activePorts; // Set, that keeps the track of no_of_ports that are active and are been used
    private int lastAssignedPort; // used to assign the next port when the request comes!
    private final StreamStore streamStore;

    /**
     * Called by the {@link UhuComms} if the streaming is Enabled in the properties file. It initializes the beginning and ending ports.
     * It initializes the PortFactory.lastAssignedPort to the statingPort.
     * @param startPort : Beginning port that can be used for Streaming. RANGE (of ports that can be used for Streaming) =  {@link PortFactory#startingPort - PortFactory#endPort }
     *  
     * @see com.bosch.upa.uhu.comms.UhuComms
     */
    public PortFactory(int startPort, StreamStore streamStore) {
        LOGGER.info("-- Inside PortFactory constructor ---- ");
        startingPort = startPort;
        activePorts = new HashSet<Integer>();
        lastAssignedPort = startPort;
        this.streamStore = streamStore;
    }

    /**
     * This method is called by ControlReceiverThread when it receives a {@link ControlMessage} with Discriminator set to "StreamRequest". This method receives the 
     * Key as [ MsgId:ServiceName:DeviceId ] from the {@link StreamRequest} and checks if any ports are available and return the int as positive value if available or -1 if not. 
     * If Uhu has already opened the {@link UhuComms#MAX_SUPPORTED_STREAMS} then it returns -1. If the port is available and the key is not duplicate( i,e the Request has
     * arrived for the first time ) then it updates the activePorts Map with the port and portsMap in StreamStore with portMapKey and port.
     *    
     * @param portMapKey key in form [MsgId:ServiceName:DeviceId] that used to keep track of active Stream
     * @return           positive integer: indicating the port that is assigned for this request. ( Port >= startingPort and  Port<= endingPort-startingPort )
     *                    -1 : if all the ports are busy.
     */
    @Override
    public int getPort(String portMapKey) {
    	synchronized (this) {
    		
    		int nextPort = -1;

            if (activePorts.size() == UhuComms.getMAX_SUPPORTED_STREAMS()) {
                LOGGER.debug("MAX STREAMS REACHED");
                return nextPort;
            }

            do {
                nextPort = startingPort + lastAssignedPort % startingPort;
                LOGGER.debug("nextport: " + nextPort);
                if (activePorts.contains(nextPort)) {
                	
                	 lastAssignedPort++;

                } else {
                	
                    activePorts.add(nextPort);
                    if (streamStore.updatePortsMap(portMapKey, nextPort)) {
                        lastAssignedPort = nextPort;
                        break;
                    } else {
                        LOGGER.error("port malfunctioning...");
                        return -1;
                    }
                   
                }
            } while (true);

            LOGGER.debug("New Port assigned: " + nextPort);
            return nextPort;
		}
    }

    /**
     * This method is called by the StreamReceivingThread  after it has received data or if data transfer fails during the transfer.
     * This method removes the record form the portsMap from {@link StreamStore} and {@link PortFactory#activePorts} from {@link PortFactory}
     * @param releasingPort : The Port that has to be released.
     * @return              : true - if the port is released successfully. false - if tried to free the port that doesn't exists. ( Port Malfunctioning ) 
     * 
     * @see com.bosch.upa.uhu.streaming.StreamResponse
     */
    @Override
    public boolean releasePort(int releasingPort) {
    	synchronized (this) {
    		
    		 if (activePorts.contains(releasingPort)) {
    	            LOGGER.debug("No of active ports before releasing: "
    	                    + activePorts.size());
    	            activePorts.remove(releasingPort);
    	            if (!streamStore.releasePort(releasingPort)) {
    	                LOGGER.debug("Error updating the ports map to free the ports");
    	            }
    	            LOGGER.debug("No of active ports after releasing: "
    	                    + activePorts.size());
    	            return true;
    	        } else {
    	            LOGGER.error("Port tried to free that doesnt exists..");
    	            streamStore.releasePort(releasingPort); // Confirmation
    	            return false;
    	        }

		}
    }

    /**
     * This method returns the ActivePortsMap that contains all the ports that are being used.
     * @return : size of the Set indicating Number of active ports that are getting streamed at this point of time.
     */
    @Override
    public int getNoOfActivePorts() {
        synchronized (this) {

            return activePorts.size();
        }
    }
}
