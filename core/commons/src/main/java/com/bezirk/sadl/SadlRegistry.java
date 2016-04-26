package com.bezirk.sadl;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.UhuDiscoveredZirk;
import com.bezirk.proxy.api.impl.UhuZirkId;
import com.bezrik.network.UhuNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.concurrent.ConcurrentMap;

/**
 * Registry Class that deals with all the maps
 */
public class SadlRegistry implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SadlRegistry.class);
    /**
     * Default location used when a new service registers. Also used to check during location matching
     */
    private final Location defaultLocation = new Location(null, null, null);
    /**
     * Stores the list of Registered UhuZirkId's of the Services.
     */
    protected HashSet<UhuZirkId> sid = null;
    /**
     * Stores the list of Services associated with the Protocol. Typically used in Discovery.
     * [Key -> Value] = [ProtocolName -> [Set of ServiceIds]]
     */
    protected ConcurrentMap<String, Set<UhuZirkId>> protocolMap = null;
    /**
     * Stores the Protocol Description associated with the ProtocolRole. Typically used in Discovery.
     * [Key -> Value] = [ProtocolName -> Protocol description]
     */
    protected ConcurrentHashMap<String, String> protocolDescMap = null;
    /**
     * Stores the serviceIds mapped to the event topics. Typically used in Sending/ Receiving Event.
     * [Key -> Value] = [eventTopic -> [Set of ServiceIds]]
     */
    protected ConcurrentMap<String, Set<UhuZirkId>> eventMap = null;
    /**
     * Stores the ServiceIds mapped to the stream topics. Typically used in Streaming.
     * [Key -> Value] = [streamTopic -> [Set of ServiceIds]]
     */
    protected ConcurrentMap<String, Set<UhuZirkId>> streamMap = null;
    /**
     * Stores the location of the Services.
     * [Key -> Value] = [UhuZirkId -> Location]
     */
    protected ConcurrentHashMap<UhuZirkId, Location> locationMap = null;

    public SadlRegistry() {
        sid = new HashSet<UhuZirkId>();
        protocolMap = new ConcurrentHashMap<String, Set<UhuZirkId>>();
        protocolDescMap = new ConcurrentHashMap<String, String>();
        eventMap = new ConcurrentHashMap<String, Set<UhuZirkId>>();
        streamMap = new ConcurrentHashMap<String, Set<UhuZirkId>>();
        locationMap = new ConcurrentHashMap<UhuZirkId, Location>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = checkNullAndComputeHashCode(prime, result, defaultLocation);
        result = checkNullAndComputeHashCode(prime, result, eventMap);
        result = checkNullAndComputeHashCode(prime, result, locationMap);
        result = checkNullAndComputeHashCode(prime, result, logger);
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

                logger.error("Unable to retrieve maps from sadlRegistry for checking.", e);

                return false;
            }
        }
        return true;
    }

    /**
     * Registers a Service
     *
     * @param serviceId of the service being registered
     * @return true if registered, false otherwise
     */
    public Boolean registerService(final UhuZirkId serviceId) {

        sid.add(serviceId);
        // Update the location!
        setLocation(serviceId, defaultLocation);
        logger.info(serviceId + " is registered successfully");
        return true;
    }


    /**
     * Subscribes the Service with the Protocol Role.
     *
     * @param serviceId of the service being subscribed
     * @param pRole     protocolRole of the service.
     * @return true if subscribed, false otherwise
     */
    public Boolean subscribeService(final UhuZirkId serviceId, final ProtocolRole pRole) {
        final String protocolName = pRole.getProtocolName();
        final String protocolDescription = pRole.getDescription();
        final String[] eventsTopics = pRole.getEventTopics();
        final String[] streamTopics = pRole.getStreamTopics();

        // Update the Protocol Map
        final Set<UhuZirkId> protocolServices;
        if (protocolMap.containsKey(protocolName)) {
            protocolServices = protocolMap.get(protocolName);
            protocolServices.add(serviceId);
        } else {
            protocolServices = new HashSet<UhuZirkId>();
            protocolServices.add(serviceId);
        }

        protocolMap.put(protocolName, protocolServices);
        // Updating protocolDescription
        if (null != protocolDescription) {

            protocolDescMap.put(protocolName, protocolDescription);
        }
        // Updating Event Map
        if (null == eventsTopics) {
            logger.info("Protocol doesnot contain any Events to subscribe");
        } else {
            for (String eventTopic : eventsTopics) {
                final HashSet<UhuZirkId> evntsResServices;
                if (eventMap.containsKey(eventTopic)) {
                    evntsResServices = (HashSet<UhuZirkId>) eventMap.get(eventTopic);
                } else {
                    evntsResServices = new HashSet<UhuZirkId>();
                }

                evntsResServices.add(serviceId);
                eventMap.put(eventTopic, evntsResServices);
            }
        }
        // Updating Stream Map
        if (null == streamTopics) {
            logger.info("Protocol does not contain any Streams to subscribe");
        } else {
            for (String streamTopic : streamTopics) {
                final HashSet<UhuZirkId> strmsResServices;
                if (streamMap.containsKey(streamTopic)) {
                    strmsResServices = (HashSet<UhuZirkId>) streamMap.get(streamTopic);
                } else {
                    strmsResServices = new HashSet<UhuZirkId>();
                }

                strmsResServices.add(serviceId);
                streamMap.put(streamTopic, strmsResServices);
            }
        }
        logger.info(protocolName + " Protocol Role subscribed successfully");
        return true;
    }


    /**
     * Un subscribes the service for the protocolRole
     *
     * @param serviceId UhuserviceId of the service being unsubscribed
     * @param role      protocolRole of the service being unsubscribed
     * @return true if unsubscribed, false otherwise
     */
    public Boolean unsubscribe(final UhuZirkId serviceId, final ProtocolRole role) {
        if (protocolMap.containsKey(role.getProtocolName())) {
            final Set<UhuZirkId> serviceIdSet = protocolMap.get(role.getProtocolName());

            if (!serviceIdSet.remove(serviceId)) {
                logger.info("Service is Trying to unsubscribe that it has not subscribed to");
                return false;
            }
            // Remove from Protocol map
            if (serviceIdSet.isEmpty()) {
                protocolMap.remove(role.getProtocolName());
                // Remove the Description
                protocolDescMap.remove(role.getProtocolName());
            } else {
                protocolMap.put(role.getProtocolName(), serviceIdSet);
            }

            // Remove all events
            if (null != role.getEventTopics()) {
                final String[] eventTopics = role.getEventTopics();
                for (String topic : eventTopics) {
                    removeTopicFromMap(topic, serviceId, eventMap);
                }
            }
            // Remove all  Streams
            if (null != role.getStreamTopics()) {
                String[] streamTopics = role.getStreamTopics();
                for (String streamTopic : streamTopics) {
                    removeTopicFromMap(streamTopic, serviceId, streamMap);
                }
            }
            return true;
        }
        logger.info(serviceId + "Service tried to Unsubscribe  " + role.getProtocolName() + " without Registration/ Service might be already unsubscribed");
        return false;
    }


    /**
     * Unregisters the Service
     *
     * @param serviceId
     * @return
     */
    public Boolean unregisterService(final UhuZirkId serviceId) {
        if (isServiceRegisterd(serviceId)) {
            // Remove the events from event Map
            removeSidFromMaps(serviceId, eventMap, false);
            // remove the steams from stream map
            removeSidFromMaps(serviceId, streamMap, false);
            // remove the protocol from protocol Map
            removeSidFromMaps(serviceId, protocolMap, true);
            // Remove the Location
            locationMap.remove(serviceId);
            //remove Sid
            sid.remove(serviceId);
            return true;
        }
        logger.info("Service tried to Unregister that doesnt exist");
        return false;
    }

    /**
     * Update the location of the Service
     *
     * @param serviceId ZirkId of the service
     * @param location  Location of the service
     * @return true if updated, false otherwise
     */
    public Boolean setLocation(final UhuZirkId serviceId, final Location location) {
        if (isServiceRegisterd(serviceId)) {
            locationMap.put(serviceId, location);
            return true;
        }
        logger.info("Tried to update the location for the Service that is not subscribed");
        return false;
    }

    /**
     * Checks if ZirkId is registed
     *
     * @param serviceId of the Service
     * @return true if sid contains ZirkId, false otherwise
     */
    public Boolean isServiceRegisterd(UhuZirkId serviceId) {
        return sid.contains(serviceId);
    }

    /**
     * Returns latest location of the service
     *
     * @param serviceId whose location needs to be known
     * @return Location of the service
     */
    public Location getLocationForService(UhuZirkId serviceId) {
        if (isServiceRegisterd(serviceId)) {
            try {
                return (defaultLocation.equals(locationMap.get(serviceId)) ? UhuCompManager.getUpaDevice().getDeviceLocation() : locationMap.get(serviceId));
            } catch (Exception e) {
                logger.error("Exception in fetching the device Location", e);
                return null;
            }
        }
        logger.error("Service Tried to fetch the location that is not Registered");
        return null;
    }

    /**
     * This method removes the topic from eventsMap and streamMap and updates them
     *
     * @param topic
     * @param serviceId
     * @param eventMap2
     */
    private void removeTopicFromMap(final String topic, final UhuZirkId serviceId, final Map<String, Set<UhuZirkId>> eventMap2) {
        if (eventMap2.containsKey(topic)) {
            HashSet<UhuZirkId> serviceIdSetEvents = (HashSet<UhuZirkId>) eventMap2.get(topic);
            if (serviceIdSetEvents.contains(serviceId)) {
                serviceIdSetEvents.remove(serviceId);
                if (serviceIdSetEvents.isEmpty()) {
                    eventMap2.remove(topic);
                } else {
                    eventMap2.put(topic, serviceIdSetEvents);
                }
            }
        }
    }


    /**
     * Removes the Sid from the maps
     *
     * @param serviceId
     * @param eventMap2
     * @param isProtocol
     */
    private void removeSidFromMaps(final UhuZirkId serviceId, final Map<String, Set<UhuZirkId>> eventMap2, final boolean isProtocol) {
        Iterator<Entry<String, Set<UhuZirkId>>> iterator = eventMap2.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Set<UhuZirkId>> entry = iterator.next();
            if (entry.getValue().contains(serviceId)) {
                entry.getValue().remove(serviceId);
                if (entry.getValue().isEmpty()) {
                    eventMap2.remove(entry.getKey());
                    if (isProtocol) {
                        protocolDescMap.remove(entry.getKey());
                    }
                }
            }
        }
    }

    /**
     * Checks if the StreamTopic is registered by any Service
     *
     * @param streamTopic
     * @param serviceId
     * @return
     */
    public Boolean isStreamTopicRegistered(String streamTopic, UhuZirkId serviceId) {
        return isServiceRegisterd(serviceId) && streamMap.containsKey(streamTopic) && streamMap.get(streamTopic).contains(serviceId);
    }

    /**
     * Checks for Discovery based on the Protocol Role and Location
     *
     * @param pRole
     * @param location
     * @return
     */
    public Set<UhuDiscoveredZirk> discoverServices(ProtocolRole pRole, Location location) {
        if (!protocolMap.containsKey(pRole.getProtocolName())) {
            logger.debug("No services are subscribed for this protocol Role");
            return null;
        }
        final HashSet<UhuDiscoveredZirk> discoveredServices = new HashSet<UhuDiscoveredZirk>();
        // Get all the services associated with the protocols
        final Set<UhuZirkId> services = protocolMap.get(pRole.getProtocolName());
        for (UhuZirkId serviceId : services) {
            Location serviceLocation = getLocationForService(serviceId);

            if (null != location && !location.subsumes(serviceLocation)) {
                logger.debug("inside if before continue, Location didnot match");
                continue;
            }
            discoveredServices.add(new UhuDiscoveredZirk(UhuNetworkUtilities.getServiceEndPoint(serviceId), null, pRole.getProtocolName(), serviceLocation));
        }
        if (discoveredServices.isEmpty()) {
            logger.debug("No services are present in the location: " + location.toString() + " subscribed to Protocol Role:  " + pRole.getProtocolName());
            return null;
        }
        return discoveredServices;
    }


    /**
     * Checks the topic is registed by the recipient
     *
     * @param topic     topic needs to be checked
     * @param recipient recipient
     * @return true if registered, false otherwise
     */
    public boolean checkUnicastEvent(String topic, UhuZirkId recipient) {
        return isServiceRegisterd(recipient) && eventMap.containsKey(topic) && eventMap.get(topic).contains(recipient);
    }

    /**
     * Returns the Set<UhuZirkId> associated with the Topic and Location
     *
     * @param topic    topic of the Event
     * @param location of the Service
     * @return Set<UhuZirkId> if the servives are present, null otherwise
     */
    public Set<UhuZirkId> checkMulticastEvent(String topic, Location location) {
        HashSet<UhuZirkId> services = null;

        if (eventMap.containsKey(topic)) {
            if (null == location) {
                return new HashSet<UhuZirkId>(eventMap.get(topic));
            }

            HashSet<UhuZirkId> tempServices = (HashSet<UhuZirkId>) eventMap.get(topic);
            services = new HashSet<UhuZirkId>();
            for (UhuZirkId serviceId : tempServices) {
                Location serviceLocation = getLocationForService(serviceId);
                if (serviceLocation != null && location.subsumes(serviceLocation)) {
                    services.add(serviceId);
                }
            }
            if (services.isEmpty()) {
                return null;
            }
        }
        return services;
    }

    /**
     * Get the list of Registered Services
     *
     * @return the list of Registered Service
     */
    public Set<UhuZirkId> getRegisteredServices() {
        return new HashSet<UhuZirkId>(sid);
    }

    /**
     * Clears all the registryclone
     */
    public void clearRegistry() {
        this.sid.clear();
        this.protocolMap.clear();
        this.protocolDescMap.clear();
        this.eventMap.clear();
        this.streamMap.clear();
        this.locationMap.clear();
    }
}
