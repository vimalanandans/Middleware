package com.bosch.upa.uhu.sadl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.addressing.Location;
import com.bezirk.api.messages.ProtocolRole;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuDiscoveredService;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * Registry Class that deals with all the maps
 */
public class SadlRegistry implements Serializable {
	/**
	 * Logger for current class
	 */
	private static final Logger log = LoggerFactory.getLogger(SadlRegistry.class);
	/**
	 * Default location used when a new service registers. Also used to check during location matching
	 */
	private final  Location defaultLocation = new Location(null, null, null);
	/**
	 * Stores the list of Registered UhuServiceId's of the Services. 
	 */
	protected HashSet<UhuServiceId> sid = null;
	/**
	 * Stores the list of Services associated with the Protocol. Typically used in Discovery. 
	 * [Key -> Value] = [ProtocolName -> [Set of ServiceIds]] 
	 */
	protected ConcurrentHashMap<String,HashSet<UhuServiceId>> protocolMap = null;
	/**
	 * Stores the Protocol Description associated with the ProtocolRole. Typically used in Discovery.
	 * [Key -> Value] = [ProtocolName -> Protocol description] 
	 */
	protected ConcurrentHashMap<String,String> protocolDescMap = null;
	/**
	 * Stores the serviceIds mapped to the event topics. Typically used in Sending/ Receiving Event.
	 * [Key -> Value] = [eventTopic -> [Set of ServiceIds]] 
	 */
	protected ConcurrentHashMap<String,HashSet<UhuServiceId>> eventMap = null;
	/**
	 * Stores the ServiceIds mapped to the stream topics. Typically used in Streaming.
	 * [Key -> Value] = [streamTopic -> [Set of ServiceIds]] 
	 */
	protected ConcurrentHashMap<String,HashSet<UhuServiceId>> streamMap = null;
	/**
	 * Stores the location of the Services.
	 * [Key -> Value] = [UhuServiceId -> Location] 
	 */
	protected ConcurrentHashMap<UhuServiceId,Location> locationMap = null;
	
	public SadlRegistry() {
		sid = new HashSet<UhuServiceId>();
		protocolMap = new ConcurrentHashMap<String,HashSet<UhuServiceId>>();
		protocolDescMap = new ConcurrentHashMap<String,String>();
		eventMap = new ConcurrentHashMap<String,HashSet<UhuServiceId>>();
		streamMap = new ConcurrentHashMap<String,HashSet<UhuServiceId>>();
		locationMap = new ConcurrentHashMap<UhuServiceId,Location>();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = checkNullAndComputeHashCode(prime, result, defaultLocation);
		result = checkNullAndComputeHashCode(prime, result, eventMap);
		result = checkNullAndComputeHashCode(prime, result, locationMap);
		result = checkNullAndComputeHashCode(prime, result, log);
		result = checkNullAndComputeHashCode(prime, result, protocolDescMap);
		result = checkNullAndComputeHashCode(prime, result, protocolMap);
		result = checkNullAndComputeHashCode(prime, result, sid);
		result = checkNullAndComputeHashCode(prime, result, streamMap);
		return result;
	}

	private int checkNullAndComputeHashCode(final int prime, int result, Object object) {
		return prime * result + ((object == null) ? 0 : object.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {

			return true;
		}
		if (obj == null) {

			return false;
		}
		if (getClass() != obj.getClass()) {

			return false;
		}
		SadlRegistry other = (SadlRegistry) obj;
		if (defaultLocation == null) {
			if (other.defaultLocation != null) {

				return false;
			}
		} else if (!defaultLocation.equals(other.defaultLocation)) {

			return false;
		}
		return checkMaps(other);
	}

	private boolean checkMaps(SadlRegistry other) {

		List<String> mapList = Arrays.asList("eventMap", "locationMap",
				"protocolDescMap", "protocolMap", "streamMap");

		for (String map : mapList) {

			Field field;
			try {
				field = SadlRegistry.class.getDeclaredField(map);

				Object currentValue = field.get(this);
				Object otherValue = field.get(other);
				if (currentValue == null) {
					if (otherValue != null) {
						return false;
					}
				} else if (!currentValue.equals(otherValue)) {
					return false;
				}
			} catch (Exception e) {
				
				log.error("Unable to retrieve maps from sadlRegistry for checking.",e);

				return false;
			}
		}
		return true;
	}

	/**
	 * Registers a Service
	 * @param serviceId of the service being registered
	 * @return true if registered, false otherwise
	 */
	public Boolean registerService(final UhuServiceId serviceId) {

		sid.add(serviceId);
		// Update the location!
		setLocation(serviceId, defaultLocation);
		log.info(serviceId + " is registered successfully");
		return true;
	}
	

	/**
	 * Subscribes the Service with the Protocol Role.
	 * @param serviceId of the service being subscribed
	 * @param pRole protocolRole of the service.
	 * @return true if subscribed, false otherwise
	 */
	public Boolean subscribeService(final UhuServiceId serviceId, final ProtocolRole pRole) {
		final String protocolName = pRole.getProtocolName();
		final String protocolDescription = pRole.getDescription();
		final String[] eventsTopics = pRole.getEventTopics();
		final String[] streamTopics = pRole.getStreamTopics();
		// Update the Protocol Map
		Set<UhuServiceId> protocolServices = null;
		if(protocolMap.containsKey(protocolName)){
			protocolServices = protocolMap.get(protocolName);
			protocolServices.add((UhuServiceId) serviceId);
		}else{
			protocolServices = new HashSet<UhuServiceId>();
			protocolServices.add((UhuServiceId) serviceId);
		}
		protocolMap.put(protocolName,(HashSet<UhuServiceId>) protocolServices);
		// Updating protocolDescription
		if(null != protocolDescription){
			
			protocolDescMap.put(protocolName, protocolDescription);
		}
		// Updating Event Map
		if(null == eventsTopics){
			log.info( "Protocol doesnot contain any Events to subscribe");
		}else{
			for(String eventTopic: eventsTopics){
				HashSet<UhuServiceId> evntsResServices = null;
				if(eventMap.containsKey(eventTopic)){
					evntsResServices = (HashSet<UhuServiceId>) eventMap.get(eventTopic);
				}else{
					evntsResServices = new HashSet<UhuServiceId>();
				}
				evntsResServices.add((UhuServiceId) serviceId);
				eventMap.put(eventTopic,evntsResServices);
			}
		}
		// Updating Stream Map
		if(null == streamTopics){
			log.info( "Protocol does not contain any Streams to subscribe");
		}else{
			for(String streamTopic: streamTopics){
				HashSet<UhuServiceId> strmsResServices = null;
				if(streamMap.containsKey(streamTopic)){
					strmsResServices = (HashSet<UhuServiceId>) streamMap.get(streamTopic);
				}else{
					strmsResServices = new HashSet<UhuServiceId>();
				}
				strmsResServices.add((UhuServiceId) serviceId);
				streamMap.put(streamTopic,strmsResServices);
			}
		}
		log.info(protocolName + " Protocol Role subscribed successfully");
		return true;
	}
	
	
	/**
	 * Un subscribes the service for the protocolRole
	 * @param serviceId UhuserviceId of the service being unsubscribed
	 * @param role protocolRole of the service being unsubscribed
	 * @return true if unsubscribed, false otherwise
	 */
	public Boolean unsubscribe(final UhuServiceId serviceId, final ProtocolRole role) {
		if(protocolMap.containsKey(role.getProtocolName())){
			HashSet<UhuServiceId> serviceIdSet = null;
			// Remove from Protocol map
			serviceIdSet = (HashSet<UhuServiceId>) protocolMap.get(role.getProtocolName());

			if(!serviceIdSet.remove(serviceId)){
				log.info("Service is Trying to unsubscribe that it has not subscribed to");
				return false;
			}
			// Remove from Protocol map
			if(serviceIdSet.isEmpty()){
				protocolMap.remove(role.getProtocolName());
				// Remove the Description
				protocolDescMap.remove(role.getProtocolName());
			}else{
				protocolMap.put(role.getProtocolName(), serviceIdSet);
			}

			// Remove all events
			if(null != role.getEventTopics()){
				final String [] eventTopics = role.getEventTopics();
				for(String topic: eventTopics){
					removeTopicFromMap(topic, serviceId, eventMap);
				}
			}
			// Remove all  Streams
			if (null != role.getStreamTopics()){
				String [] streamTopics = role.getStreamTopics();
				for(String streamTopic : streamTopics){
					removeTopicFromMap(streamTopic, serviceId, streamMap);
				}
			}
			return true;
		}
		log.info(serviceId + "Service tried to Unsubscribe  " + role.getProtocolName() +" without Registration/ Service might be already unsubscribed");
		return false;
	}
	
	
	/**
	 * Unregisters the Service
	 * @param serviceId
	 * @return
	 */
	public Boolean unregisterService(final UhuServiceId serviceId) {
		if(isServiceRegisterd(serviceId)){
			// Remove the events from event Map
			removeSidFromMaps(serviceId, eventMap, false);
			// remove the steams from stream map
			removeSidFromMaps(serviceId, streamMap,false);
			// remove the protocol from protocol Map
			removeSidFromMaps(serviceId, protocolMap, true);
			// Remove the Location
			locationMap.remove(serviceId);
			//remove Sid
			sid.remove(serviceId);
			return true;
		}
		log.info("Service tried to Unregister that doesnt exist");
		return false;
	}
	
	/**
	 * Update the location of the Service
	 * @param serviceId ServiceId of the service
	 * @param location Location of the service
	 * @return true if updated, false otherwise
	 */
	public Boolean setLocation(final UhuServiceId serviceId, final Location location) {
		if(isServiceRegisterd(serviceId)){
			locationMap.put(serviceId,location);
			return true;
		}
		log.info("Tried to update the location for the Service that is not subscribed");
		return false;
	}
	
	/**
	 * Checks if ServiceId is registed
	 * @param serviceId of the Service
	 * @return true if sid contains ServiceId, false otherwise
	 */
	public Boolean isServiceRegisterd(UhuServiceId serviceId) {
		return sid.contains(serviceId);
	}
	
	/**
	 * Returns latest location of the service
	 * @param serviceId whose location needs to be known
	 * @return Location of the service
	 */
	public Location getLocationForService(UhuServiceId serviceId) {
		if(isServiceRegisterd(serviceId)){
			try{
				return (defaultLocation.equals(locationMap.get(serviceId)) ? UhuCompManager.getUpaDevice().getDeviceLocation(): locationMap.get(serviceId));
			}catch(Exception e){
				log.error("Exception in fetching the device Location", e);
				return null;
			}
		}
		log.error("Service Tried to fetch the location that is not Registered");
		return null;
	}
	
	/**
	 * This method removes the topic from eventsMap and streamMap and updates them
	 * @param topic 
	 * @param serviceId
	 * @param eventMap2
	 */
	private void removeTopicFromMap(final String topic, final UhuServiceId serviceId ,final Map<String, HashSet<UhuServiceId>> eventMap2){
		if(eventMap2.containsKey(topic)){
			HashSet<UhuServiceId> serviceIdSetEvents = (HashSet<UhuServiceId>) eventMap2.get(topic);
			if(serviceIdSetEvents.contains(serviceId)){
				serviceIdSetEvents.remove(serviceId);
				if(serviceIdSetEvents.isEmpty()){
					eventMap2.remove(topic);
				}else{
					eventMap2.put(topic, serviceIdSetEvents);
				}
			}
		}
	}
	

	/**
	 * Removes the Sid from the maps
	 * @param serviceId
	 * @param eventMap2
	 * @param isProtocol
	 */
	private void removeSidFromMaps(final UhuServiceId serviceId, final Map<String, HashSet<UhuServiceId>> eventMap2, final boolean isProtocol){
		Iterator<Entry<String, HashSet<UhuServiceId>>> iterator = eventMap2.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String,HashSet<UhuServiceId>> entry = iterator.next();
			if(entry.getValue().contains(serviceId)){
				entry.getValue().remove(serviceId);
				if(entry.getValue().isEmpty()){
					eventMap2.remove(entry.getKey());
					if(isProtocol){
						protocolDescMap.remove(entry.getKey());
					}
				}
			}
		}		
	}
	
	/**
	 * Checks if the StreamTopic is registered by any Service
	 * @param streamTopic
	 * @param serviceId
	 * @return
	 */
	public Boolean isStreamTopicRegistered(String streamTopic, UhuServiceId serviceId) {
		return isServiceRegisterd(serviceId) && streamMap.containsKey(streamTopic) && streamMap.get(streamTopic).contains(serviceId);
	}
	
	/**
	 * Checks for Discovery based on the Protocol Role and Location
	 * @param pRole
	 * @param location
	 * @return
	 */
	public Set<UhuDiscoveredService> discoverServices(ProtocolRole pRole, Location location) {
		if(!protocolMap.containsKey(pRole.getProtocolName())){
			log.debug("No services are subscribed for this protocol Role");
			return null;
		}
		final HashSet<UhuDiscoveredService> discoveredServices = new HashSet<UhuDiscoveredService>();
		// Get all the services associated with the protocols
		final Set<UhuServiceId> services = protocolMap.get(pRole.getProtocolName());
		for(UhuServiceId serviceId: services){
			Location serviceLocation = getLocationForService(serviceId);

			if(null != location && !location.subsumes(serviceLocation)){
				log.debug("inside if before continue, Location didnot match");
				continue;
			}
			discoveredServices.add(new UhuDiscoveredService(UhuNetworkUtilities.getServiceEndPoint((UhuServiceId)serviceId), null, pRole.getProtocolName(),serviceLocation));
		}
		if(discoveredServices.isEmpty()){
			log.debug("No services are present in the location: " + location.toString()+ " subscribed to Protocol Role:  " + pRole.getProtocolName());
			return null;
		}
		return discoveredServices;
	}

	
	/**
	 * Checks the topic is registed by the recipient
	 * @param topic topic needs to be checked
	 * @param recipient recipient 
	 * @return true if registered, false otherwise
	 */
	public boolean checkUnicastEvent(String topic, UhuServiceId recipient) {
		return isServiceRegisterd(recipient) && eventMap.containsKey(topic) && eventMap.get(topic).contains(recipient);
	}
	
	/**
	 * Returns the Set<UhuServiceId> associated with the Topic and Location
	 * @param topic topic of the Event
	 * @param location of the Service
	 * @return Set<UhuServiceId> if the servives are present, null otherwise
	 */
	public Set<UhuServiceId> checkMulticastEvent(String topic, Location location) {
		HashSet<UhuServiceId> services = null;

		if(eventMap.containsKey(topic)){
			if(null == location){
				return new HashSet<UhuServiceId>((HashSet<UhuServiceId>)eventMap.get(topic));
			}
			
			HashSet<UhuServiceId> tempServices = (HashSet<UhuServiceId>) eventMap.get(topic);
			services = new HashSet<UhuServiceId>();
			for(UhuServiceId serviceId: tempServices){
				Location serviceLocation = getLocationForService(serviceId);
				if(serviceLocation != null && location.subsumes(serviceLocation)){
						services.add(serviceId);
				}
			}
			if(services.isEmpty()){
				return null;
			}
		}
		return services;
	}
	
	/**
	 * Get the list of Registered Services
	 * @return the list of Registered Service
	 */
	public Set<UhuServiceId> getRegisteredServices() {
		return new HashSet<UhuServiceId>(sid);
	}

	/**
	 * Clears all the registryclone
	 */
	public void clearRegistry(){
		this.sid.clear();
		this.protocolMap.clear();
		this.protocolDescMap.clear();
		this.eventMap.clear();
		this.streamMap.clear();
		this.locationMap.clear();
	}
}
