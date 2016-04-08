package com.bosch.upa.uhu.sadl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.addressing.Location;
import com.bezirk.api.messages.ProtocolRole;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.control.messages.EventLedger;
import com.bosch.upa.uhu.control.messages.MulticastHeader;
import com.bosch.upa.uhu.control.messages.UnicastHeader;
import com.bosch.upa.uhu.discovery.DiscoveryManager;
import com.bosch.upa.uhu.messagehandler.EventIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamStatusMessage;
import com.bosch.upa.uhu.persistence.ISadlPersistence;
import com.bosch.upa.uhu.proxy.api.impl.SubscribedRole;
import com.bosch.upa.uhu.proxy.api.impl.UhuDiscoveredService;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.remotelogging.messages.UhuLoggingMessage;
import com.bosch.upa.uhu.remotelogging.queues.LoggingQueueManager;
import com.bosch.upa.uhu.remotelogging.spherefilter.FilterLogMessages;
import com.bosch.upa.uhu.remotelogging.status.LoggingStatus;
import com.bosch.upa.uhu.remotelogging.util.Util;
import com.bosch.upa.uhu.rest.BezirkRestCallBack;
import com.bosch.upa.uhu.rest.BezirkRestCallBackImpl;
import com.bosch.upa.uhu.rest.BezirkRestCommsManager;
import com.bosch.upa.uhu.util.UhuValidatorUtility;

/**
 * This class implements the ISadlRegistry, ISadlRegistryLookup Interfaces. This class is used by ProxyForServices (by casting ISadlRegistry)
 * EventSender/ EventReceiver/ ControlSender/ ControlReceiver by casting ISadlRegistryLookup.
 */
public class UhuSadlManager implements ISadlRegistry, ISadlRegistryLookup, ISadlControlReceiver,ISadlEventReceiver{
	/**
	 * Logger for current class
	 */
	private final Logger log = LoggerFactory.getLogger(UhuSadlManager.class);

	protected ISadlPersistence sadlPersistence = null;
	
	protected SadlRegistry sadlRegistry = null;
	
	protected IUhuComms uhuComms = null;

    private final Date currentDate = new Date();

	private DiscoveryManager discoveryManager = null;

	public UhuSadlManager(ISadlPersistence sadlPersistence) {
		this.sadlPersistence = sadlPersistence;
		loadSadlRegistry();
	}

	/** initialize the object references for future use */
	public void initSadlManager(IUhuComms uhuComms){
		this.uhuComms = uhuComms;
		initServiceDiscovery(uhuComms);
	}

	/** moved the init discovery from comms layer to sphere.
	 * because this is out of comms layer */
	public void initServiceDiscovery(IUhuComms uhuComms)
	{

		discoveryManager = new DiscoveryManager(this, uhuComms);

		discoveryManager.initDiscovery();

	}

	/* (non-Javadoc)
	 * @see com.bosch.upa.uhu.api.sadl.ISadlRegistry#registerService(com.bosch.upa.uhu.api.addressing.UhuServiceId)
	 */
	@Override
	public Boolean registerService(final UhuServiceId serviceId) {
		if(!UhuValidatorUtility.checkUhuServiceId((UhuServiceId) serviceId)){
			log.error("Invalid UhuServiceId");
			return false;
		}
		if(isServiceRegisterd(serviceId)){
			log.info(serviceId + " Service is already registered");
			return false;
		}
		if(sadlRegistry.registerService(serviceId)){
			persistSadlRegistry();
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.bosch.upa.uhu.sadl.ISadlRegistry#subscribeService(com.bosch.upa.uhu.api.addressing.UhuServiceId, ProtocolRole)
	 */
	@Override
	public Boolean subscribeService(final UhuServiceId serviceId, final ProtocolRole pRole) {
		if(!UhuValidatorUtility.checkUhuServiceId((UhuServiceId) serviceId) || !UhuValidatorUtility.checkProtocolRole((SubscribedRole) pRole)){
			log.error("Invalid Subscription, Validation failed");
			return false;
		}
		if(!isServiceRegisterd(serviceId)){
			log.info("Service tried to subscribe without Registration");
			return false;
		}
		
		if(sadlRegistry.subscribeService(serviceId, pRole)){
			persistSadlRegistry();
			return true;
		}
		return false;
	}

	@Override
	public Boolean unsubscribe(final UhuServiceId serviceId, final ProtocolRole role) {
		if(!UhuValidatorUtility.checkUhuServiceId((UhuServiceId) serviceId) || !UhuValidatorUtility.checkProtocolRole((SubscribedRole) role) ){
			log.error("Invalid UnSubscription, Validation failed");
			return false;
		}
		if(sadlRegistry.unsubscribe(serviceId, role)){
			persistSadlRegistry();
			return true;
		}
		return false;
	}

	@Override
	public Boolean unregisterService(final UhuServiceId serviceId) {
		if(!UhuValidatorUtility.checkUhuServiceId((UhuServiceId) serviceId) ){
			log.error("Invalid UnRegistration, Validation failed");
			return false;
		}
		if(sadlRegistry.unregisterService(serviceId)){
			persistSadlRegistry();
			return true;
		}
		return false;
	}


	@Override
	public Boolean setLocation(final UhuServiceId serviceId, final Location location) {
		if(sadlRegistry.setLocation(serviceId, location)){
			persistSadlRegistry();
			return true;
		}
		return false;
	}

	@Override
	public Boolean isServiceRegisterd(UhuServiceId serviceId) {
		if(UhuValidatorUtility.checkUhuServiceId((UhuServiceId) serviceId)){
			return sadlRegistry.isServiceRegisterd(serviceId);
		}
		return false;
	}

	@Override
	public Location getLocationForService(UhuServiceId serviceId) {
		return sadlRegistry.getLocationForService(serviceId);
	}





	@Override
	public Boolean isStreamTopicRegistered(String streamTopic, UhuServiceId serviceId) {
		if(!UhuValidatorUtility.checkForString(streamTopic) || !UhuValidatorUtility.checkUhuServiceId((UhuServiceId)serviceId)){
			log.error("Stream Topic or service Id is invalid");
			return false;
		}
		return sadlRegistry.isStreamTopicRegistered(streamTopic, serviceId);
	}

	// SERVICE-NAME NEEDS TO BE FILLED
	@Override
	public Set<UhuDiscoveredService> discoverServices(ProtocolRole pRole, Location location) {
		if(!UhuValidatorUtility.checkProtocolRole((SubscribedRole) pRole)){
			log.error("Discarding Discovery Lookup as ProtocolRole is invalid");
			return null;
		}
		return sadlRegistry.discoverServices(pRole, location);
	}


    @Override
    public boolean processEvent(final EventLedger eLedger) {

        Set<UhuServiceId> invokeList = fetchInvokeList(eLedger);

        if(null == invokeList || invokeList.isEmpty()) {
			log.debug("No services are present to respond to the request");
			return false;
		}
		// FIXME: commented decrypt to test the comms-zyre-jni, enable it later
        if(!eLedger.getIsLocal() && !decryptMsg(eLedger)) {
            return false;
        }

        if(LoggingStatus.isLoggingEnabled() && FilterLogMessages.checkSphere(eLedger.getHeader().getSphereName())){
            sendRemoteLogMessage(eLedger);
        }
        
    	// give a callback to appropriate service..
    	giveCallback(eLedger, invokeList);	
        
        return true;
    }

	private void giveCallback(final EventLedger eLedger,
			Set<UhuServiceId> invokeList) {
		// check if the service exists in that sphere then give callback
        for(UhuServiceId serviceId: invokeList){
        	if(invokeList.contains(new UhuServiceId("SPOOFED")) && 
        			eLedger.getHeader().getSphereName().equals(BezirkRestCommsManager.getInstance().getSlectedSphereName())){
            	//send the response to HTTPComms also..
            	BezirkRestCallBack callBack = new BezirkRestCallBackImpl();
            	callBack.callBackForResponse(eLedger);
            	
            }else if(UhuCompManager.getSphereForSadl().isServiceInSphere(serviceId, eLedger.getHeader().getSphereName())){
                EventIncomingMessage eCallbackMessage = new EventIncomingMessage(serviceId,eLedger.getHeader().getSenderSEP(),
                        eLedger.getSerializedMessage(),eLedger.getHeader().getTopic(),eLedger.getHeader().getUniqueMsgId());
                UhuCompManager.getplatformSpecificCallback().onIncomingEvent(eCallbackMessage);
            }else{
            	log.debug("Unknown Service ID!!!!!");
            }
        }
	}

	private Set<UhuServiceId> fetchInvokeList(final EventLedger eLedger) {
		Set<UhuServiceId> invokeList = null;
		if(eLedger.getIsMulticast()){
			MulticastHeader mHeader = (MulticastHeader)eLedger.getHeader();
			Location targetLocation = mHeader.getAddress()==null?null:mHeader.getAddress().getLocation();
			invokeList = this.checkMulticastEvent(mHeader.getTopic(), targetLocation);	
		}else{
			UnicastHeader uHeader = (UnicastHeader)eLedger.getHeader();

			//here i can check for the spoofed event and bypass the sadl validation
			if(uHeader != null && uHeader.getRecipient().serviceId.getUhuEventId() != null && uHeader.getRecipient().serviceId.getUhuServiceId().equals("THIS-SERVICE-ID-IS-HTTP-SPOOFED")){
				invokeList = new HashSet<UhuServiceId>();
				invokeList.add(new UhuServiceId("SPOOFED"));
			}else if(this.checkUnicastEvent(uHeader.getTopic(), uHeader.getRecipient().serviceId)){
				invokeList = new HashSet<UhuServiceId>();
				invokeList.add(uHeader.getRecipient().serviceId);
			}
		}
		return invokeList;
	}
    /** decrypt the event */
    private Boolean decryptMsg(EventLedger eLedger){
        // Decrypt the event message
        final String decryptedEventMsg = UhuCompManager.getSphereForSadl().decryptSphereContent(eLedger.getHeader().getSphereName(), eLedger.getEncryptedMessage());
        if (!UhuValidatorUtility.checkForString(decryptedEventMsg)) {
            log.debug( "Header Decryption Failed: sphereId-"+eLedger.getHeader().getSphereName() + " may not exist");

            return false;
        }
        eLedger.setSerializedMessage(decryptedEventMsg);
        return true;
    }
    /** route the events logging message to */
    private void sendRemoteLogMessage(EventLedger eLedger){
        try {
            LoggingQueueManager.loadLogSenderQueue(new UhuLoggingMessage(eLedger.getHeader().getSphereName(),
                    String.valueOf(currentDate.getTime()), UhuCompManager.getUpaDevice().getDeviceName(),
                    Util.CONTROL_RECEIVER_VALUE, eLedger.getHeader().getUniqueMsgId(), eLedger.getHeader().getTopic(), Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name(), Util.LOGGING_VERSION).serialize());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

	public boolean checkUnicastEvent(String topic, UhuServiceId recipient) {
		if(!UhuValidatorUtility.checkForString(topic) || !UhuValidatorUtility.checkUhuServiceId((UhuServiceId)recipient)){
			log.error("Unicast Event Check failed -> topic or Recipient is not valid");
			return false;
		}
		return sadlRegistry.checkUnicastEvent(topic, recipient);
	}

	// Return a HashSet<UhuServiceId> by creating a new one otherwise the receiving components can modify it!

	public Set<UhuServiceId> checkMulticastEvent(String topic, Location location) {
		if(!UhuValidatorUtility.checkForString(topic)){
			log.error("Event Topic or Recipient is valid");
			return null;
		}
		return sadlRegistry.checkMulticastEvent(topic, location);
	}

	@Override
	public Set<UhuServiceId> getRegisteredServices() {
		return sadlRegistry.getRegisteredServices();
	}

	private void loadSadlRegistry(){
		try {
			sadlRegistry = sadlPersistence.loadSadlRegistry();
		} catch (Exception e) {
			log.error("Error in loading sadl registry from persistence \n", e);
		}
	}
	@Override
	public boolean processStreamStatus(StreamStatusMessage streamStatusNotifciation) {
		UhuCompManager.getplatformSpecificCallback().onStreamStatus(streamStatusNotifciation);
		return true;
	}

	@Override
	public boolean processNewStream(StreamIncomingMessage streamData){
		UhuCompManager.getplatformSpecificCallback().onIncomingStream(streamData);
		return true;
	}

	private void persistSadlRegistry(){
		try {
			sadlPersistence.persistSadlRegistry();
		} catch (Exception e) {
			log.error("Error in storing data \n", e);
		}
	}



}
