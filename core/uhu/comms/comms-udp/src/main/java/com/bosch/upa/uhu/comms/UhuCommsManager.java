package com.bosch.upa.uhu.comms;

import com.bosch.upa.uhu.streaming.UhuStreamManager;
import com.bosch.upa.uhu.comms.udp.listeners.ControlMulticastListener;
import com.bosch.upa.uhu.comms.udp.listeners.ControlUnicastListener;
import com.bosch.upa.uhu.comms.udp.listeners.EventMulticastListener;
import com.bosch.upa.uhu.comms.udp.listeners.EventUnicastListener;
import com.bosch.upa.uhu.comms.udp.threads.ControlReceiverThread;
import com.bosch.upa.uhu.comms.udp.threads.ControlSenderThread;
import com.bosch.upa.uhu.comms.udp.threads.EventReceiverThread;
import com.bosch.upa.uhu.comms.udp.threads.EventSenderThread;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.EventLedger;
import com.bosch.upa.uhu.control.messages.Ledger;
import com.bosch.upa.uhu.control.messages.logging.LoggingServiceMessage;
import com.bosch.upa.uhu.logging.LogServiceMessageHandler;
import com.bosch.upa.uhu.pipe.core.PipeManager;
import com.bosch.upa.uhu.sadl.UhuSadlManager;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord;

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
 * */

public class UhuCommsManager implements IUhuCommsLegacy{

    private static final Logger log = LoggerFactory.getLogger(UhuCommsManager.class);

    MessageDispatcher msgDispatcher = null;
    
    private UhuStreamManager uhuStreamManager =null;

    /** Queues for the comms Deprecated*/

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
	
	private Thread eSenderThread = null;
	
	private Thread cReceiverThread = null;
	
	private Thread cSenderThread = null;
	
	//Discovery Cleaner thread
	//private Thread discThread;

    // Sphere Discovery thread.
    //this is Moved to uhusphere
    // private Thread sphereDiscThread;

    CommCtrlReceiver ctrlReceiver = new CommCtrlReceiver();

    UhuSadlManager uhuSadlManager = null;

	LogServiceMessageHandler logServiceMsgHandler = null;
    /**
     * Version Callback that will be used to inform the platfroms when there is mismatch in versions.
     * This parameter will be injected in all the components that will be checking for versions to
     * be compatible before they are processed.
     * @author Vijet Badigannavar
     */
    private ICommsNotification notification = null;

	private IUhuSphereForSadl sphereForSadl;

	//private ServiceMessageHandler uhuCallback = null;

	public UhuCommsManager(){

	}

	/** Create the queues for message sending and receiving data 
	 * 
	 * */
	@Override
	public boolean initComms(CommsProperties commsProperties, InetAddress addr,
              UhuSadlManager uhuSadlManager, PipeManager pipe){
		
        this.uhuSadlManager = uhuSadlManager;

		msgDispatcher = new MessageDispatcher(uhuSadlManager);

		receiverMessageQueue = new MessageQueue();
		
		controlSenderQueue =  new MessageQueue();
		
		controlReceiverQueue = new MessageQueue();

        sendingMessageQueue = new MessageQueue();
		
		//Start Listener Sockets
		try {
			eMSocket = new MulticastSocket(UhuComms.getMULTICAST_PORT());
			eUSocket = new DatagramSocket(UhuComms.getUNICAST_PORT(), addr);
			cMSocket = new MulticastSocket(UhuComms.getCTRL_MULTICAST_PORT());
			cUSocket = new DatagramSocket(UhuComms.getCTRL_UNICAST_PORT(), addr);	
			
			eMSocket.setInterface(addr);
			
			cMSocket.setInterface(addr);
			
		} catch (SocketException e){
			log.error("unable to get socket ports : is it already running?" , e);
			return false;
		}
		catch (IOException e) {
			log.error("unable to get socket ports : is it already running?" , e);
			return false;
		}

		//Start Event Listeners
		//@modifed by vijet, added a parameter mismatchVersionCallback to all the listener threads
		eMListenerThread = new Thread( new EventMulticastListener(eMSocket, this,notification));
		
		eUListenerThread = new Thread( new EventUnicastListener(eUSocket, this,notification));
		
		cMListenerThread = new Thread( new ControlMulticastListener(cMSocket, this,notification));
		
		cUListenerThread = new Thread( new ControlUnicastListener(cUSocket, this,notification));
		
		//Start Receiver Threads
		eReceiverThread = new Thread( new EventReceiverThread(msgDispatcher, receiverMessageQueue));
		
		ControlReceiverThread ctrlReceiverThread = new ControlReceiverThread(msgDispatcher, controlReceiverQueue);
		
		cReceiverThread = new Thread( ctrlReceiverThread);

		
		EventSenderThread senderThread = new EventSenderThread(this, sendingMessageQueue, pipe);

        eSenderThread = new Thread(senderThread);

        cSenderThread = new Thread( new ControlSenderThread(this,controlSenderQueue));

        if(UhuComms.isStreamingEnabled()){

        	this.uhuStreamManager = new UhuStreamManager(this,msgDispatcher,uhuSadlManager);
        	uhuStreamManager.initStreams();

        }

		// register the logging service message
		msgDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.LoggingServiceMessage,ctrlReceiver);

		return true;
	}

    class CommCtrlReceiver implements ICtrlMsgReceiver{
        @Override
        // FIXME : remove the below Log related quickfix, by moving the implementation to respective module
        public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
            switch (id)
            {
                case LoggingServiceMessage:
                    //log.debug("<<<<<<<<  LOGGING MESSAGE RECEIVED FROM LOGGING SERVICE  >>>>>>>>>");
                    log.debug("ReceivedLogMessage-> " + serializedMsg);
                    try{
                        final LoggingServiceMessage loggingServiceMsg = ControlMessage.deserialize(serializedMsg, LoggingServiceMessage.class);

                        if(null == logServiceMsgHandler){
                            logServiceMsgHandler = new LogServiceMessageHandler();
                        }
                        logServiceMsgHandler.handleLogServiceMessage(loggingServiceMsg);
                    }catch(Exception e){
                        log.error("Error in Deserializing LogServiceMessage", e);
                    }
                    break;
                default:
                    log.error("Unknown control message > "+id);
                    return false;
            }
            return true;
        }
    }

	@Override
	public boolean closeComms() {
		//Close sockets
		if(eMSocket!= null)
			eMSocket.close();
		
		if(eUSocket!= null)
			eUSocket.close();
		
		if(cMSocket!= null)
			cMSocket.close();
		
		if(cUSocket!= null)
			cUSocket.close();
		
		if(UhuComms.isStreamingEnabled()){
			
			if (uhuStreamManager!=null) {
				uhuStreamManager.endStreams();
			}
		}
		return true;
	}
	
	@Override
	public boolean startComms() {
		// start the listeners
		if(eMListenerThread != null) 
			eMListenerThread.start();
		
		if(eUListenerThread != null)
			eUListenerThread.start();
		
		if(cMListenerThread != null)
			cMListenerThread.start();
		
		if(cUListenerThread != null)
			cUListenerThread.start();
        
        
		if(eReceiverThread != null)
			eReceiverThread.start();
		
		if(cReceiverThread != null)
			cReceiverThread.start();
		
		if(cSenderThread != null)
			cSenderThread.start();
		
		
		if(eSenderThread != null)
			eSenderThread.start();
		
		/*if(discThread != null)
            discThread.start();

      /*  if(sphereDiscThread != null)
            sphereDiscThread.start(); */

		 if(UhuComms.isStreamingEnabled()){

			 if (uhuStreamManager!=null) {
				uhuStreamManager.startStreams();
			}
		 }
		
		return true;
	}

	@Override
	public boolean stopComms() {
		//Interrupt Listener threads
		if(eMListenerThread != null)
			eMListenerThread.interrupt();
		
		if(eUListenerThread != null)
			eUListenerThread.interrupt();
		
		if(cMListenerThread != null)
			cMListenerThread.interrupt();
		
		if(cUListenerThread != null)
			cUListenerThread.interrupt();

		//Interrupt Sender and Receiver threads
		if(eReceiverThread != null)
			eReceiverThread.interrupt();
		
		if(eSenderThread != null)
			eSenderThread.interrupt();
		
		if(cReceiverThread != null)
			cReceiverThread.interrupt();
		
		if(cSenderThread != null)
			cSenderThread.interrupt();


		if(UhuComms.isStreamingEnabled()){
			
			if (uhuStreamManager!=null) {
				uhuStreamManager.endStreams();
			}
		}
		
		return true;
	}
	
	/* unused
	 * private boolean diagnose(){
	if(!eMListenerThread.getState().equals(Thread.State.RUNNABLE)){
		log.error( "EVENT MULTICAST LISTENER THREAD HAS STALLED");
		return false;
	}
	if(!eUListenerThread.getState().equals(Thread.State.RUNNABLE)){
		log.error( "EVENT UNICAST LISTENER THREAD HAS STALLED");
		return false;
	}
	if(!cMListenerThread.getState().equals(Thread.State.RUNNABLE)){
		log.error( "CONTROL MULTICAST LISTENER THREAD HAS STALLED");
		return false;
	}
	if(!cUListenerThread.getState().equals(Thread.State.RUNNABLE)){
		log.error( "CONTROL UNICAST LISTENER THREAD HAS STALLED");
		return false;
	}
	return true;
}*/

	
	/** set the receiver event and control queues (message IN from external world )*/
	@Override
	public void setReceiverQueues(MessageQueue eventQueue, MessageQueue controlQueue)
	{
		receiverMessageQueue = eventQueue;
		controlReceiverQueue = controlQueue;
	}
	
	/** set the sender event and control queues (message OUT to external world )*/
	@Override
	public void setSenderQueues(MessageQueue eventQueue, MessageQueue controlQueue){
		sendingMessageQueue = eventQueue;
        controlSenderQueue = controlQueue;
	}

	/** get the control sender (message OUT to external world )*/
	@Override
	public MessageQueue getControlSenderQueue(MessageQueue eventQueue){
		return controlSenderQueue;
	}
	
	/** get the control receiver (message IN to external world )*/
	@Override
	public MessageQueue getControlReceiverQueue(MessageQueue eventQueue){
		return controlReceiverQueue;
	}
	
	/** get the event sender (message OUT to external world )*/
	@Override
	public MessageQueue getEventSenderQueue(MessageQueue eventQueue){
		return sendingMessageQueue;
	}
	
	/** get the event receiver (message IN to external world )*/
	@Override
	public MessageQueue getEventReceiverQueue(MessageQueue eventQueue){
		return receiverMessageQueue;
	}
	/**
	 * This is the message queue for stream requests on the receiver side
	 * @return MessageQueue
	 */
	public MessageQueue getStreamingMessageQueue() {
		
		if(uhuStreamManager !=null){
			
			return uhuStreamManager.getStreamingMessageQueue();
		}
		return null;
	}
	/**
	 * This is the message queue for stream requests  on the sender side
	 * @param streamingMessageQueue
	 */
	public void setStreamingMessageQueue(MessageQueue streamingMessageQueue) {        

		if (uhuStreamManager !=null) {
			
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
	
	/** get the queue*/
	MessageQueue getQueue(COMM_QUEUE_TYPE queueType){
		
		MessageQueue queue = null;
		
		switch(queueType)
		{
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

	/** send the control message */
	@Override
	public boolean sendControlMessage(Ledger message) {
		
		controlSenderQueue.addToQueue(message);
		
		return true;
	}
	
	/** send the event message */
	@Override
	public boolean sendEventMessage(Ledger message) {
		
		sendingMessageQueue.addToQueue(message);
		
		return true;
	}
	
	/** send the Stream ledger message */
	@Override
	public boolean sendStreamMessage(Ledger message) {
		
		if (uhuStreamManager !=null) {
			
			uhuStreamManager.sendStreamMessage(message);
			return true;
		}else{
			
			log.error("UhuStreamManager is not initialized.");
			return false;
		}
		
	}

    @Override
     public boolean sendStream(String uniqueKey) {
       
		if (uhuStreamManager != null) {
			
			return uhuStreamManager.sendStream(uniqueKey);
		} else {

			log.error("UhuStreamManager is not initialized.");
			return false;
		}

    }

    @Override
    public boolean registerStreamBook(String key, StreamRecord sRecord) {

    	if (uhuStreamManager!=null) {
    		
			return uhuStreamManager.registerStreamBook(key, sRecord);
		}else{
			
			log.error("UhuStreamManager is not initialized.");
			return false;
		}
    
    }

    @Override
    public IPortFactory getPortFactory() {
        
    	if (uhuStreamManager!=null) {
    		
			return uhuStreamManager.getPortFactory();
			
		} else{
			
			log.error("UhuStreamManager is not initialized.");
			return null;
		}
        		
    }

    @Override
	public boolean sendMessage(Ledger message) {

		if(message instanceof ControlLedger)
			return sendControlMessage(message);
		else if (message instanceof EventLedger)
			return sendEventMessage(message);
		else // stream ledger // hopefully there are no other types
			return sendStreamMessage(message);
	
	}
    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, ICtrlMsgReceiver receiver)
    {
        return msgDispatcher.registerControlMessageReceiver(id,receiver);
    }

	@Override
	public boolean registerNotification(ICommsNotification notification) {

		if(null != notification){
			this.notification = notification;
			return true;
		}
		return false;

	}

	/* (non-Javadoc)
	 * @see com.bosch.upa.uhu.comms.IUhuComms#setSphereForSadl(com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl)
	 */
	@Override
	public void setSphereForSadl(IUhuSphereForSadl uhuSphere) {
		
		
		this.sphereForSadl = uhuSphere;
		uhuStreamManager.setSphereForSadl(sphereForSadl);
	}
	
	/**
	 * TODO needs to be implemented.
	 */
	@Override
	public boolean restartComms() {
		// TODO Auto-generated method stub
		return false;
	}

}
