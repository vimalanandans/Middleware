package com.bezirk.comms;

import com.bezirk.comms.udp.listeners.ControlMulticastListener;
import com.bezirk.comms.udp.listeners.ControlUnicastListener;
import com.bezirk.comms.udp.listeners.EventMulticastListener;
import com.bezirk.comms.udp.listeners.EventUnicastListener;
import com.bezirk.comms.udp.threads.ControlReceiverThread;
import com.bezirk.comms.udp.threads.ControlSenderThread;
import com.bezirk.comms.udp.threads.EventReceiverThread;
import com.bezirk.comms.udp.threads.EventSenderThread;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.logging.LogServiceMessageHandler;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.streaming.UhuStreamManager;
import com.bezirk.streaming.control.Objects.StreamRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Uhu Communication manager
 * this handles all the queue, sockets, receiver threads etc etc
 * Note : this code is handling many legacy workaround
 * when implementing new IUhuComms, make sure you clean this. Vimal
 *
 * @modified Vijet Badigannavar added IUhuVersionMismatchCallback as a member variable
 */

public class UhuCommsManager implements IUhuCommsLegacy {
    private static final Logger logger = LoggerFactory.getLogger(UhuCommsManager.class);

    MessageDispatcher msgDispatcher = null;
    CommCtrlReceiver ctrlReceiver = new CommCtrlReceiver();
    UhuSadlManager uhuSadlManager = null;
    LogServiceMessageHandler logServiceMsgHandler = null;
    private UhuStreamManager uhuStreamManager = null;
    /**
     * Queues for the comms Deprecated
     */

    private MessageQueue sendingMessageQueue = null;
    private MessageQueue receiverMessageQueue = null;
    private MessageQueue controlSenderQueue = null;
    private MessageQueue controlReceiverQueue = null;
    //Sockets for listeners
    private MulticastSocket eMSocket = null;
    private DatagramSocket eUSocket = null;
    private MulticastSocket cMSocket = null;
    private DatagramSocket cUSocket = null;
    //Listener Threads
    private Thread eMListenerThread = null;
    private Thread eUListenerThread = null;
    private Thread cMListenerThread = null;
    private Thread cUListenerThread = null;
    //Receiver and Sender threads
    private Thread eReceiverThread = null;

    //Discovery Cleaner thread
    //private Thread discThread;

    // sphere Discovery thread.
    //this is Moved to uhusphere
    // private Thread sphereDiscThread;
    private Thread eSenderThread = null;
    private Thread cReceiverThread = null;
    private Thread cSenderThread = null;
    /**
     * Version Callback that will be used to inform the platfroms when there is mismatch in versions.
     * This parameter will be injected in all the components that will be checking for versions to
     * be compatible before they are processed.
     *
     * @author Vijet Badigannavar
     */
    private CommsNotification notification = null;

    //private ZirkMessageHandler uhuCallback = null;

    public UhuCommsManager() {

    }

    /**
     * Create the queues for message sending and receiving data
     */
    @Override
    public boolean initComms(CommsProperties commsProperties, InetAddress addr,
                             UhuSadlManager uhuSadlManager, PipeManager pipe) {

        this.uhuSadlManager = uhuSadlManager;

        msgDispatcher = new MessageDispatcher(uhuSadlManager);

        receiverMessageQueue = new MessageQueue();

        controlSenderQueue = new MessageQueue();

        controlReceiverQueue = new MessageQueue();

        sendingMessageQueue = new MessageQueue();

        //Start Listener Sockets
        try {
            eMSocket = new MulticastSocket(BezirkComms.getMULTICAST_PORT());
            eUSocket = new DatagramSocket(BezirkComms.getUNICAST_PORT(), addr);
            cMSocket = new MulticastSocket(BezirkComms.getCTRL_MULTICAST_PORT());
            cUSocket = new DatagramSocket(BezirkComms.getCTRL_UNICAST_PORT(), addr);

            eMSocket.setInterface(addr);

            cMSocket.setInterface(addr);

        } catch (SocketException e) {
            logger.error("unable to get socket ports : is it already running?", e);
            return false;
        } catch (IOException e) {
            logger.error("unable to get socket ports : is it already running?", e);
            return false;
        }

        //Start Event Listeners
        //@modifed by vijet, added a parameter mismatchVersionCallback to all the listener threads
        eMListenerThread = new Thread(new EventMulticastListener(eMSocket, this, notification));

        eUListenerThread = new Thread(new EventUnicastListener(eUSocket, this, notification));

        cMListenerThread = new Thread(new ControlMulticastListener(cMSocket, this, notification));

        cUListenerThread = new Thread(new ControlUnicastListener(cUSocket, this, notification));

        //Start Receiver Threads
        eReceiverThread = new Thread(new EventReceiverThread(msgDispatcher, receiverMessageQueue));

        ControlReceiverThread ctrlReceiverThread = new ControlReceiverThread(msgDispatcher, controlReceiverQueue);

        cReceiverThread = new Thread(ctrlReceiverThread);


        EventSenderThread senderThread = new EventSenderThread(this, sendingMessageQueue, pipe);

        eSenderThread = new Thread(senderThread);

        cSenderThread = new Thread(new ControlSenderThread(this, controlSenderQueue));

        if (BezirkComms.isStreamingEnabled()) {

            this.uhuStreamManager = new UhuStreamManager(this, msgDispatcher, uhuSadlManager);
            uhuStreamManager.initStreams();

        }

        // register the logging zirk message
        msgDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.LoggingServiceMessage, ctrlReceiver);

        return true;
    }

    @Override
    public boolean closeComms() {
        //Close sockets
        if (eMSocket != null)
            eMSocket.close();

        if (eUSocket != null)
            eUSocket.close();

        if (cMSocket != null)
            cMSocket.close();

        if (cUSocket != null)
            cUSocket.close();

        if (BezirkComms.isStreamingEnabled()) {

            if (uhuStreamManager != null) {
                uhuStreamManager.endStreams();
            }
        }
        return true;
    }

    @Override
    public boolean startComms() {
        // start the listeners
        if (eMListenerThread != null)
            eMListenerThread.start();

        if (eUListenerThread != null)
            eUListenerThread.start();

        if (cMListenerThread != null)
            cMListenerThread.start();

        if (cUListenerThread != null)
            cUListenerThread.start();


        if (eReceiverThread != null)
            eReceiverThread.start();

        if (cReceiverThread != null)
            cReceiverThread.start();

        if (cSenderThread != null)
            cSenderThread.start();


        if (eSenderThread != null)
            eSenderThread.start();

		/*if(discThread != null)
            discThread.start();

      /*  if(sphereDiscThread != null)
            sphereDiscThread.start(); */

        if (BezirkComms.isStreamingEnabled()) {

            if (uhuStreamManager != null) {
                uhuStreamManager.startStreams();
            }
        }

        return true;
    }

    @Override
    public boolean stopComms() {
        //Interrupt Listener threads
        if (eMListenerThread != null)
            eMListenerThread.interrupt();

        if (eUListenerThread != null)
            eUListenerThread.interrupt();

        if (cMListenerThread != null)
            cMListenerThread.interrupt();

        if (cUListenerThread != null)
            cUListenerThread.interrupt();

        //Interrupt Sender and Receiver threads
        if (eReceiverThread != null)
            eReceiverThread.interrupt();

        if (eSenderThread != null)
            eSenderThread.interrupt();

        if (cReceiverThread != null)
            cReceiverThread.interrupt();

        if (cSenderThread != null)
            cSenderThread.interrupt();


        if (BezirkComms.isStreamingEnabled()) {

            if (uhuStreamManager != null) {
                uhuStreamManager.endStreams();
            }
        }

        return true;
    }

    /**
     * set the receiver event and control queues (message IN from external world )
     */
    @Override
    public void setReceiverQueues(MessageQueue eventQueue, MessageQueue controlQueue) {
        receiverMessageQueue = eventQueue;
        controlReceiverQueue = controlQueue;
    }
	
	/* unused
	 * private boolean diagnose(){
	if(!eMListenerThread.getState().equals(Thread.State.RUNNABLE)){
		logger.error( "EVENT MULTICAST LISTENER THREAD HAS STALLED");
		return false;
	}
	if(!eUListenerThread.getState().equals(Thread.State.RUNNABLE)){
		logger.error( "EVENT UNICAST LISTENER THREAD HAS STALLED");
		return false;
	}
	if(!cMListenerThread.getState().equals(Thread.State.RUNNABLE)){
		logger.error( "CONTROL MULTICAST LISTENER THREAD HAS STALLED");
		return false;
	}
	if(!cUListenerThread.getState().equals(Thread.State.RUNNABLE)){
		logger.error( "CONTROL UNICAST LISTENER THREAD HAS STALLED");
		return false;
	}
	return true;
}*/

    /**
     * set the sender event and control queues (message OUT to external world )
     */
    @Override
    public void setSenderQueues(MessageQueue eventQueue, MessageQueue controlQueue) {
        sendingMessageQueue = eventQueue;
        controlSenderQueue = controlQueue;
    }

    /**
     * get the control sender (message OUT to external world )
     */
    @Override
    public MessageQueue getControlSenderQueue(MessageQueue eventQueue) {
        return controlSenderQueue;
    }

    /**
     * get the control receiver (message IN to external world )
     */
    @Override
    public MessageQueue getControlReceiverQueue(MessageQueue eventQueue) {
        return controlReceiverQueue;
    }

    /**
     * get the event sender (message OUT to external world )
     */
    @Override
    public MessageQueue getEventSenderQueue(MessageQueue eventQueue) {
        return sendingMessageQueue;
    }

    /**
     * get the event receiver (message IN to external world )
     */
    @Override
    public MessageQueue getEventReceiverQueue(MessageQueue eventQueue) {
        return receiverMessageQueue;
    }

    /**
     * This is the message queue for stream requests on the receiver side
     *
     * @return MessageQueue
     */
    public MessageQueue getStreamingMessageQueue() {

        if (uhuStreamManager != null) {

            return uhuStreamManager.getStreamingMessageQueue();
        }
        return null;
    }

    /**
     * This is the message queue for stream requests  on the sender side
     *
     * @param streamingMessageQueue
     */
    public void setStreamingMessageQueue(MessageQueue streamingMessageQueue) {

        if (uhuStreamManager != null) {

            uhuStreamManager.setStreamingMessageQueue(streamingMessageQueue);
        }

    }

    @Override
    public void addToQueue(COMM_QUEUE_TYPE queueType, Ledger message) {
        getQueue(queueType).addToQueue(message);
    }

    @Override
    public void removeFromQueue(COMM_QUEUE_TYPE queueType, Ledger message) {
        getQueue(queueType).removeFromQueue(message);

    }

    @Override
    public ArrayList<Ledger> getQueueData(COMM_QUEUE_TYPE queueType) {
        return getQueue(queueType).getQueue();

    }

    /**
     * get the queue
     */
    MessageQueue getQueue(COMM_QUEUE_TYPE queueType) {

        MessageQueue queue = null;

        switch (queueType) {
            case CONTROL_SEND_QUEUE:
                queue = controlSenderQueue;
                break;

            case CONTROL_RECEIVE_QUEUE:
                queue = controlReceiverQueue;
                break;

            case EVENT_SEND_QUEUE:
                queue = sendingMessageQueue;
                break;

            case EVENT_RECEIVE_QUEUE:
                queue = receiverMessageQueue;
                break;

            case STREAMING_QUEUE:
                if (uhuStreamManager != null) {
                    queue = uhuStreamManager.getStreamingMessageQueue();
                }
                break;
            default:
                queue = null;
                break;
        }
        return queue;
    }

    /**
     * send the control message
     */
    @Override
    public boolean sendControlMessage(Ledger message) {

        controlSenderQueue.addToQueue(message);

        return true;
    }

    /**
     * send the event message
     */
    @Override
    public boolean sendEventMessage(Ledger message) {

        sendingMessageQueue.addToQueue(message);

        return true;
    }

    /**
     * send the Stream ledger message
     */
    @Override
    public boolean sendStreamMessage(Ledger message) {

        if (uhuStreamManager != null) {

            uhuStreamManager.sendStreamMessage(message);
            return true;
        } else {

            logger.error("UhuStreamManager is not initialized.");
            return false;
        }

    }

    @Override
    public boolean sendStream(String uniqueKey) {

        if (uhuStreamManager != null) {

            return uhuStreamManager.sendStream(uniqueKey);
        } else {

            logger.error("UhuStreamManager is not initialized.");
            return false;
        }

    }

    @Override
    public boolean registerStreamBook(String key, StreamRecord sRecord) {

        if (uhuStreamManager != null) {

            return uhuStreamManager.registerStreamBook(key, sRecord);
        } else {

            logger.error("UhuStreamManager is not initialized.");
            return false;
        }

    }

    @Override
    public IPortFactory getPortFactory() {

        if (uhuStreamManager != null) {

            return uhuStreamManager.getPortFactory();

        } else {

            logger.error("UhuStreamManager is not initialized.");
            return null;
        }

    }

    @Override
    public boolean sendMessage(Ledger message) {

        if (message instanceof ControlLedger)
            return sendControlMessage(message);
        else if (message instanceof EventLedger)
            return sendEventMessage(message);
        else // stream ledger // hopefully there are no other types
            return sendStreamMessage(message);

    }

    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver) {
        return msgDispatcher.registerControlMessageReceiver(id, receiver);
    }

    @Override
    public boolean registerNotification(CommsNotification notification) {

        if (null != notification) {
            this.notification = notification;
            return true;
        }
        return false;

    }

    /* (non-Javadoc)
     * @see IUhuComms#setSphereForSadl(IUhuSphereForSadl)
     */
    @Override
    public void setSphereForSadl(IUhuSphereForSadl uhuSphere) {
        uhuStreamManager.setSphereForSadl(uhuSphere);
    }

    /**
     * TODO needs to be implemented.
     */
    @Override
    public boolean restartComms() {
        // TODO Auto-generated method stub
        return false;
    }

    class CommCtrlReceiver implements CtrlMsgReceiver {
        @Override
        // FIXME : remove the below Log related quickfix, by moving the implementation to respective module
        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
            switch (id) {
                case LoggingServiceMessage:
                    //logger.debug("<<<<<<<<  LOGGING MESSAGE RECEIVED FROM LOGGING SERVICE  >>>>>>>>>");
                    logger.debug("ReceivedLogMessage-> " + serializedMsg);
                    try {
                        final LoggingServiceMessage loggingServiceMsg = ControlMessage.deserialize(serializedMsg, LoggingServiceMessage.class);

                        if (null == logServiceMsgHandler) {
                            logServiceMsgHandler = new LogServiceMessageHandler();
                        }
                        logServiceMsgHandler.handleLogServiceMessage(loggingServiceMsg);
                    } catch (Exception e) {
                        logger.error("Error in Deserializing LogServiceMessage", e);
                    }
                    break;
                default:
                    logger.error("Unknown control message > " + id);
                    return false;
            }
            return true;
        }
    }

}
