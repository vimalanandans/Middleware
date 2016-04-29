/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.streaming.store;

import com.bezirk.streaming.control.Objects.StreamRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * This class is used for book keeping of stream requests.{@link this#streamBook} is used to keep track of all the stream requests that is used at the sender side (SenderSide).
 *  {@link this#portsMap} is used to keep the track of all the ports that have been addressed for the StreamRequests from the zirk. (Receiving side)
 *
 */
public class StreamStore {
    private static final Logger logger = LoggerFactory.getLogger(StreamStore.class);

    // Maps stream_id:StreamRecord
    // Updated during sending control message
    // Read after receiving a StreamResponse message
    private final Map<String, StreamRecord> streamBook = new HashMap<String, StreamRecord>();

    // Maps key:[msgId:ZirkEndPoint] --- value: [Integer]
    // Updated by the PortFactory during assigning a new Port
    // Read during each ControlMessage received to check if the request is
    // always processed!
    private final Map<String, Integer> portsMap = new HashMap<String, Integer>();

    /*
     * This method used by the {@link PortFactory} to update the {@link
     * this#portsMap} when a {@link StreamRequest} is received and the ports are
     * available.
     * 
     * @param portMapKey - [ MsgId:ZirkEndPoint:DeviceID ] that uniquely
     * identifies the port that is mapped to the StreamRequest. It is used to
     * avoid duplication of the requests.
     * 
     * @param value - Port value
     * 
     * @return status of the update. <code>true</code> if successful
     * 
     * @see com.bosch.upa.bezirk.comms.udp.streaming.PortFactory
     */
    public boolean updatePortsMap(String portMapKey, int value) {

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
            logger.debug("portsmap updated with key : value:" + "key:" + portMapKey
                    + " value:" + value);
            return true;

        }

    }

    /**
     * This method called by the {@link
     * com.bosch.upa.bezirk.comms.udp.streaming.StreamReceivingThread} to release
     * the port after data is transferred.
     * 
     * @param port - Port to be released
     * 
     * @return status of the update. <code>true>/code> if success
     */
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
                logger.error("port key tried to remove that doesn't exist");
            }
            return updatedPortMap;
        }

    }

    /*
     * This method returns the {@link this#portsMap}, that maps
     * <[MsagId:ServiceName:DeviceId],Port>
     * 
     * @return the PortsMap<Integer, StreamRecord>
     */
    public Map<String, Integer> getPortsMap() {
        synchronized (this) {

            return portsMap;
        }
    }

    /*
     * This method registers the {@link StreamRecord} into {@link
     * com.bosch.upa.uhu.comms.udp.streaming.StreamStore}. It will be called by
     * the {@link com.bosch.upa.uhu.Proxy.android.BezirkProxyForServiceAPI} before
     * it creates the StreamRequest
     * 
     * @param key
     * 
     * @param sRecord
     * 
     * @return the status of Registration. if the key is new returns true. If
     * the key is duplicate returns false
     */
    public final boolean registerStreamBook(String key, StreamRecord sRecord) {
        synchronized (this) {
            if (streamBook.containsKey(key)) {
                logger.error("Cannot register stream");
                return false;
            } else {
                streamBook.put(key, sRecord);
                return true;
            }
        }
    }

    /*
     * This method checks the {@link StreamRequest} for Duplication. This method
     * is called by the ControlReceivingThread} after receiving the {@link
     * StreamRequest}. This method is used to check if the {@link
     * StreamRequest}. If it is a new one , then it spawns a thread and
     * construct the {@link StreamResponse} and send the response. If the {@link
     * StreamRequest} is duplicate then construct the {@link StreamResponse} and
     * send, without starting the thread
     * 
     * @param isDuplicateStreamRequestKey key [ msgId:ServiceName ] to check for
     * duplication of StreamMessages
     * 
     * @return true if the PortsMap already contains the Key or false if the key
     * is not present
     * 
     * @see com.bosch.upa.uhu.comms.udp.streaming.StreamSendingThread
     */
    public final boolean checkStreamRequestForDuplicate(
            String isDuplicateStreamRequestKey) {
        return portsMap.containsKey(isDuplicateStreamRequestKey);
    }

    public final int getAssignedPort(String key) {
        return portsMap.containsKey(key) ? portsMap.get(key) : -1;
    }

    public final StreamRecord popStreamRecord(String uniqueKey) {
        return (null == uniqueKey) ? null : streamBook.remove(uniqueKey);
    }

}
