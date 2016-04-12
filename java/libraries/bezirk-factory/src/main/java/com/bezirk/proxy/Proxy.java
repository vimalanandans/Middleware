package com.bezirk.proxy;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.IBezirk;
import com.bezirk.api.IBezirkListener;
import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.Location;
import com.bezirk.api.addressing.Pipe;
import com.bezirk.api.addressing.PipePolicy;
import com.bezirk.api.addressing.ServiceEndPoint;
import com.bezirk.api.addressing.ServiceId;
import com.bezirk.api.messages.Event;
import com.bezirk.api.messages.ProtocolRole;
import com.bezirk.api.messages.Stream;
import com.bezirk.callback.pc.CBkForServicePC;
import com.bezirk.callback.pc.IBoradcastReceiver;
import com.bezirk.persistence.IUhuProxyPersistence;
import com.bezirk.persistence.UhuProxyRegistry;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.proxy.pc.ProxyforServices;
import com.bezirk.proxy.registration.ServiceRegistration;
import com.bezirk.starter.MainService;
import com.bezirk.starter.UhuConfig;
import com.bezirk.util.UhuValidatorUtility;

public class Proxy implements IBezirk {
	private static final Logger log = LoggerFactory.getLogger(Proxy.class);
	private final ProxyforServices proxy;
	private final ProxyUtil proxyUtil;

	protected final HashMap<UhuServiceId, DiscoveryBookKeeper> dListenerMap = new HashMap<UhuServiceId, DiscoveryBookKeeper>();
	protected final HashMap<UhuServiceId,HashSet<IBezirkListener>> sidMap = new HashMap<UhuServiceId,HashSet<IBezirkListener>>();
	protected final HashMap<String, HashSet<IBezirkListener>> eventListenerMap = new HashMap<String, HashSet<IBezirkListener>>();
	protected final HashMap<String, HashSet<IBezirkListener>> streamListenerMap = new HashMap<String, HashSet<IBezirkListener>>();
	// Stream
	private short streamFactory = 0;
	protected final HashMap<String,String> activeStreams = new HashMap<String,String>();
	private static int discoveryCount = 0; // keep track of Discovery Id

	private final IUhuProxyPersistence proxyPersistence;
	private UhuProxyRegistry uhuProxyRegistry =null;
	private final MainService mainService;
	
	   //Create object for listener, discoveryId pair
    class DiscoveryBookKeeper {
        private final int discoveryId;
        private final IBezirkListener listener;
        DiscoveryBookKeeper(int id, IBezirkListener listener){
            this.discoveryId = id;
            this.listener = listener;
        }
        public int getDiscoveryId() {
            return discoveryId;
        }
        public IBezirkListener getListener() {
            return listener;
        }

    }
	
	public Proxy(){
		proxy = new ProxyforServices();
		proxyUtil = new ProxyUtil();
		mainService = new MainService(proxy, null);
		final IBoradcastReceiver brForService = new BRForService(activeStreams, dListenerMap,
		        eventListenerMap, sidMap, streamListenerMap);
		CBkForServicePC uhuPcCallback = new CBkForServicePC(brForService);
        mainService.startStack(uhuPcCallback);
		proxyPersistence = mainService.getUhuProxyPersistence();
		try {
		    uhuProxyRegistry = proxyPersistence.loadUhuProxyRegistry();
		} catch (Exception e) {
		    log.error("Error in Loding UhuProxyRegistry", e);
		    System.exit(0);
		}
	}

	public Proxy(UhuConfig uhuConfig){
		proxy = new ProxyforServices();
		proxyUtil = new ProxyUtil();
		mainService = new MainService(proxy, uhuConfig);
		BRForService brForService = new BRForService(activeStreams, dListenerMap,
                eventListenerMap, sidMap, streamListenerMap);
        CBkForServicePC uhuPcCallback = new CBkForServicePC(brForService);
        mainService.startStack(uhuPcCallback);
		proxyPersistence = mainService.getUhuProxyPersistence();
		try {
			uhuProxyRegistry = proxyPersistence.loadUhuProxyRegistry();
		} catch (Exception e) {
			log.error("Error in Loding UhuProxyRegistry", e);
			System.exit(0);
		}
	}

	@Override
	public ServiceId registerService(String serviceName) {
		log.trace("inside RegisterService");
		if (serviceName == null) {
			log.error("Service name Cannot be null during Registration");
			return null;
		}
		
		String  serviceIdAsString = uhuProxyRegistry.getUhuServiceId(serviceName);
		
		if(null == serviceIdAsString){
			serviceIdAsString = ServiceRegistration.generateUniqueServiceID()+":"+serviceName;
			uhuProxyRegistry.updateUhuServiceId(serviceName,serviceIdAsString);
			try {
				proxyPersistence.persistUhuProxyRegistry();
			} catch (Exception e) {
				log.error("Error in persisting the information", e);
			}
		}
		log.info("Service-Id-> " + serviceIdAsString);
		final UhuServiceId serviceId = new UhuServiceId(serviceIdAsString);
		// Register with Uhu
		proxy.registerService(serviceId, serviceName);
		return serviceId;
	}

	@Override
	public void unregisterService(ServiceId myId) {
		if(null == myId){
			log.error("Trying to UnRegister with null ID");
			return;
		}
		// Clear the Persistence by removing the UhuServiceId of the unregistering Service
		UhuServiceId sId = (UhuServiceId) myId;
		uhuProxyRegistry.deleteUhuServiceId(sId.getUhuServiceId());
		try {
			proxyPersistence.persistUhuProxyRegistry();
		} catch (Exception e) {
			log.error("Error in persisting the information", e);
		}
		proxy.unregister((UhuServiceId)myId);
	}

	@Override
	public void subscribe(final ServiceId subscriber, final ProtocolRole pRole, final IBezirkListener listener) {
		if (null == pRole.getProtocolName() || pRole.getProtocolName().isEmpty() || null == listener || null == subscriber) {
			log.error("Check for ProtocolRole/ UhuListener/ServiceId for null or empty values");
			return;
		}
		if (null == pRole.getEventTopics() && null == pRole.getStreamTopics()) {
			log.error( "ProtocolRole doesn't have any Events/ Streams to subscribe");
			return;
		}
        if (UhuValidatorUtility.isObjectNotNull(pRole.getEventTopics())) {
            proxyUtil.addTopicsToMaps(subscriber, pRole.getEventTopics(),
                    listener,sidMap, eventListenerMap, "Event");
        } else {
            log.info("No Events to Subscribe");
        }
        if (UhuValidatorUtility.isObjectNotNull(pRole.getStreamTopics())) {

            proxyUtil.addTopicsToMaps(subscriber, pRole.getStreamTopics(),
                    listener,sidMap, streamListenerMap, "Stream");

        } else {
            log.info("No Streams to Subscribe");
        }
		// Send the intent
		SubscribedRole subRole = new SubscribedRole(pRole);

		//Subscribe to protocol
		proxy.subscribeService((UhuServiceId)subscriber, subRole);
	}

	@Override
	public void unsubscribe(ServiceId subscriber,
			ProtocolRole pRole) {
		if(null == subscriber || null == pRole){
			log.error("Null Values for unsubscribe method");
			return;
		}
		proxy.unsubscribe((UhuServiceId)subscriber, new SubscribedRole(pRole));

	}

	@Override
	public void sendEvent(ServiceId sender, Address target,
			Event event) {
		// Check for sending the target!
		if (null == event || null == sender) {
			log.error("Check for null in target or Event or sender");
			return;
		}
		proxy.sendMulticastEvent((UhuServiceId)sender, target, event.serialize());       
	}

	@Override
	public void sendEvent(ServiceId sender,
			ServiceEndPoint receiver,
			Event event) {
		if (null == receiver || null == event || null == sender) {
			log.error("Check for null in receiver or Event or sender");
			return;
		}	
		proxy.sendUnicastEvent((UhuServiceId)sender, (UhuServiceEndPoint)receiver, event.serialize());
	}

	@Override
	public short sendStream(ServiceId sender,
			ServiceEndPoint receiver,
			Stream s, PipedOutputStream p) {
		short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
		activeStreams.put(((UhuServiceId)sender).getUhuServiceId()+streamId, s.topic);
		UhuServiceEndPoint recipientSEP = (UhuServiceEndPoint) receiver;        
		proxy.sendStream((UhuServiceId)sender, recipientSEP, s.serialize(), streamId);
		return streamId;
	}

	@Override
	public short sendStream(ServiceId sender, ServiceEndPoint receiver, Stream s, String filePath) {
		if (null == receiver || null == s || null == filePath || filePath.isEmpty() || s.topic.isEmpty()) {
			log.error("Check for null values in sendStream()/ Topic might be Empty");
			return (short) -1;
		}

		File tempFile = new File(filePath);
		if (!tempFile.exists()) {
			log.error( " No file found at the location: " + filePath);
			return (short) -1;
		}
		short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
		activeStreams.put(((UhuServiceId)sender).getUhuServiceId()+streamId, s.topic);
		proxy.sendStream((UhuServiceId) sender, (UhuServiceEndPoint) receiver, s.serialize(), filePath, streamId);
		return streamId;
	}

	@Override
	public void requestPipe(ServiceId requester, Pipe p, PipePolicy allowedIn,
			PipePolicy allowedOut, IBezirkListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getPipePolicy(Pipe p,
			IBezirkListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void discover(ServiceId service, Address scope,
			ProtocolRole pRole, long timeout,
			int maxDiscovered, IBezirkListener listener) {
		// update discovery map
		discoveryCount = (++discoveryCount) % Integer.MAX_VALUE;
		dListenerMap.put((UhuServiceId) service, new DiscoveryBookKeeper(discoveryCount, listener));	
		proxy.discover((UhuServiceId)service, scope, new SubscribedRole(pRole), discoveryCount, timeout, maxDiscovered);

	}

	@Override
	public void setLocation(ServiceId service, Location location) {
		if (null == location) {
			log.error("Location is null or Empty, Services cannot set the location as Null");
			return;
		}	      
		//Set
		proxy.setLocation((UhuServiceId)service, location);
	}

}
