package com.bezirk.comms;

import com.bezirk.control.messages.Ledger;

import java.util.ArrayList;

/**
 * Added by Vimal
 * This class is created to support old legcy implementation access to queues inside comms
 * <p/>
 * Use it only with in udp communication
 */
@Deprecated // this should not be used outside comms
public interface IUhuCommsLegacy extends IUhuComms {

    /**
     * set the receiver event and control queues (message IN from external world )
     */
    public void setReceiverQueues(MessageQueue eventQueue, MessageQueue controlQueue);

    /**
     * set the sender event and control queues (message OUT to external world )
     */
    public void setSenderQueues(MessageQueue eventQueue, MessageQueue controlQueue);

    /**
     * get the control sender (message OUT to external world )
     */
    public MessageQueue getControlSenderQueue(MessageQueue eventQueue);

    /**
     * get the control receiver (message IN to external world )
     */
    public MessageQueue getControlReceiverQueue(MessageQueue eventQueue);

    /**
     * get the event sender (message OUT to external world )
     */
    public MessageQueue getEventSenderQueue(MessageQueue eventQueue);

    /**
     * get the event receiver (message IN to external world )
     */
    public MessageQueue getEventReceiverQueue(MessageQueue eventQueue);

    /**
     * This is the message queue for stream requests on the receiver side
     */
    public MessageQueue getStreamingMessageQueue();

    /**
     * This is the message queue for stream requests on the receiver side
     */
    public void setStreamingMessageQueue(MessageQueue streamQueue);

    public void addToQueue(COMM_QUEUE_TYPE queueType, Ledger message);

    public void removeFromQueue(COMM_QUEUE_TYPE queueType, Ledger message);

    public ArrayList<Ledger> getQueueData(COMM_QUEUE_TYPE queueType);

    /* send the control message */
    public boolean sendControlMessage(Ledger message);

    /* send the event message */
    public boolean sendEventMessage(Ledger message);

    /**
     * send the stream message
     */
    public boolean sendStreamMessage(Ledger message);

    public IPortFactory getPortFactory();

    enum COMM_QUEUE_TYPE {
        CONTROL_SEND_QUEUE,
        CONTROL_RECEIVE_QUEUE,
        EVENT_SEND_QUEUE,
        EVENT_RECEIVE_QUEUE,
        STREAMING_QUEUE
    }

    /** set sphere discovery */
    //public boolean initDiscovery(IUhuSphereDiscovery sphereDiscHandler);

}
