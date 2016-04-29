package com.bezirk.sadl;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezrik.network.BezirkNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
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
     * Default location used when a new zirk registers. Also used to check during location matching
     */
    private final Location defaultLocation = new Location(null, null, null);
    /**
     * Stores the list of Registered BezirkZirkId's of the Services.
     */
    protected HashSet<BezirkZirkId> sid = null;
    /**
     * Stores the list of Services associated with the Protocol. Typically used in Discovery.
     * [Key -> Value] = [ProtocolName -> [Set of ServiceIds]]
     */
    protected ConcurrentMap<String, Set<BezirkZirkId>> protocolMap = null;
    /**
     * Stores the Protocol Description associated with the ProtocolRole. Typically used in Discovery.
     * [Key -> Value] = [ProtocolName -> Protocol description]
     */
    protected ConcurrentHashMap<String, String> protocolDescMap = null;
    /**
     * Stores the serviceIds mapped to the event topics. Typically used in Sending/ Receiving Event.
     * [Key -> Value] = [eventTopic -> [Set of ServiceIds]]
     */
    protected ConcurrentMap<String, Set<BezirkZirkId>> eventMap = null;
    /**
     * Stores the ServiceIds mapped to the stream topics. Typically used in Streaming.
     * [Key -> Value] = [streamTopic -> [Set of ServiceIds]]
     */
    protected ConcurrentMap<String, Set<BezirkZirkId>> streamMap = null;
    /**
     * Stores the location of the Services.
     * [Key -> Value] = [BezirkZirkId -> Location]
     */
    protected ConcurrentHashMap<BezirkZirkId, Location> locationMap = null;

    public SadlRegistry() {
        sid = new HashSet<BezirkZirkId>();
        protocolMap = new ConcurrentHashMap<String, Set<BezirkZirkId>>();
        protocolDescMap = new ConcurrentHashMap<String, String>();
        eventMap = new ConcurrentHashMap<String, Set<BezirkZirkId>>();
        streamMap = new ConcurrentHashMap<String, Set<BezirkZirkId>>();
        locationMap = new ConcurrentHashMap<BezirkZirkId, Location>();
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
     * Registers a Zirk
     *
     * @param serviceId of the zirk being registered
     * @return true if registered, false otherwise
     */
    public Boolean registerService(final BezirkZirkId serviceId) {

        sid.add(serviceId);
        // Update the location!
        setLocation(serviceId, defaultLocation);
        logger.info(serviceId + " is registered successfully");
        return true;
    }


    /**
     * Subscribes the Zirk with the Protocol Role.
     *
     * @param serviceId of the zirk being subscribed
     * @param pRole     protocolRole of the zirk.
     * @return true if subscribed, false otherwise
     */
    public Boolean subscribeService(final BezirkZirkId serviceId, final ProtocolRole pRole) {
        final String protocolName = pRole.getProtocolName();
        final String protocolDescription = pRole.getDescription();
        final String[] eventsTopics = pRole.getEventTopics();
        final String[] streamTopics = pRole.getStreamTopics();

        // Update the Protocol Map
        final Set<BezirkZirkId> protocolServices;
        if (protocolMap.containsKey(protocolName)) {
            protocolServices = protocolMap.get(protocolName);
            protocolServices.add(serviceId);
        } else {
            protocolServices = new HashSet<BezirkZirkId>();
            protocolServices.add(serviceId);
        }

        protocolMap.put(protocolName, protocolServices);
        // Updating protocolDescription
        if (null != protocolDescription) {

            protocolDescMap.put(protocolName, protocolDescription);
        }
        // Updating Event Map
        if (null == eventsTopics) {
            logger.info("Protocol does not contain any Events to subscribe");
        } else {
            for (String eventTopic : eventsTopics) {
                final HashSet<BezirkZirkId> eventsResServices;
                if (eventMap.containsKey(eventTopic)) {
                    eventsResServices = (HashSet<BezirkZirkId>) eventMap.get(eventTopic);
                } else {
                    eventsResServices = new HashSet<BezirkZirkId>();
                }

                eventsResServices.add(serviceId);
                eventMap.put(eventTopic, eventsResServices);
            }
        }
        // Updating Stream Map
        if (null == streamTopics) {
            logger.info("Protocol does not contain any Streams to subscribe");
        } else {
            for (String streamTopic : streamTopics) {
                final HashSet<BezirkZirkId> strmsResServices;
                if (streamMap.containsKey(streamTopic)) {
                    strmsResServices = (HashSet<BezirkZirkId>) streamMap.get(streamTopic);
                } else {
                    strmsResServices = new HashSet<BezirkZirkId>();
                }

                strmsResServices.add(serviceId);
                streamMap.put(streamTopic, strmsResServices);
            }
        }
        logger.info(protocolName + " Protocol Role subscribed successfully");
        return true;
    }


    /**
     * Un subscribes the zirk for the protocolRole
     *
     * @param zirkId BezirkZirkId of the zirk being unsubscribed
     * @param role      protocolRole of the zirk being unsubscribed
     * @return true if unsubscribed, false otherwise
     */
    public Boolean unsubscribe(final BezirkZirkId zirkId, final ProtocolRole role) {
        if (protocolMap.containsKey(role.getProtocolName())) {
            final Set<BezirkZirkId> serviceIdSet = protocolMap.get(role.getProtocolName());

            if (!serviceIdSet.remove(zirkId)) {
                logger.info("Zirk is Trying to unsubscribe that it has not subscribed to");
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
                    removeTopicFromMap(topic, zirkId, eventMap);
                }
            }
            // Remove all  Streams
            if (null != role.getStreamTopics()) {
                String[] streamTopics = role.getStreamTopics();
                for (String streamTopic : streamTopics) {
                    removeTopicFromMap(streamTopic, zirkId, streamMap);
                }
            }
            return true;
        }
        logger.info(zirkId + "Zirk tried to Unsubscribe  " + role.getProtocolName() + " without Registration/ Zirk might be already unsubscribed");
        return false;
    }


    /**
     * Unregisters the Zirk
     *
     * @param serviceId
     * @return
     */
    public Boolean unregisterService(final BezirkZirkId serviceId) {
        if (isServiceRegistered(serviceId)) {
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
        logger.info("Zirk tried to Unregister that does not exist");
        return false;
    }

    /**
     * Update the location of the Zirk
     *
     * @param serviceId ZirkId of the zirk
     * @param location  Location of the zirk
     * @return true if updated, false otherwise
     */
    public Boolean setLocation(final BezirkZirkId serviceId, final Location location) {
        if (isServiceRegistered(serviceId)) {
            locationMap.put(serviceId, location);
            return true;
        }
        logger.info("Tried to update the location for the Zirk that is not subscribed");
        return false;
    }

    /**
     * Checks if ZirkId is registered
     *
     * @param zirkId of the Zirk
     * @return true if sid contains ZirkId, false otherwise
     */
    public Boolean isServiceRegistered(BezirkZirkId zirkId) {
        return sid.contains(zirkId);
    }

    /**
     * Returns latest location of the zirk
     *
     * @param serviceId whose location needs to be known
     * @return Location of the zirk
     */
    public Location getLocationForService(BezirkZirkId serviceId) {
        if (isServiceRegistered(serviceId)) {
            try {
                return (defaultLocation.equals(locationMap.get(serviceId)) ? BezirkCompManager.getUpaDevice().getDeviceLocation() : locationMap.get(serviceId));
            } catch (Exception e) {
                logger.error("Exception in fetching the device Location", e);
                return null;
            }
        }
        logger.error("Zirk Tried to fetch the location that is not Registered");
        return null;
    }

    /**
     * This method removes the topic from eventsMap and streamMap and updates them
     *
     * @param topic
     * @param serviceId
     * @param eventMap2
     */
    private void removeTopicFromMap(final String topic, final BezirkZirkId serviceId, final Map<String, Set<BezirkZirkId>> eventMap2) {
        if (eventMap2.containsKey(topic)) {
            HashSet<BezirkZirkId> serviceIdSetEvents = (HashSet<BezirkZirkId>) eventMap2.get(topic);
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
    private void removeSidFromMaps(final BezirkZirkId serviceId, final Map<String, Set<BezirkZirkId>> eventMap2, final boolean isProtocol) {
        for (Entry<String, Set<BezirkZirkId>> entry : eventMap2.entrySet()) {
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
     * Checks if the StreamTopic is registered by any Zirk
     *
     * @param streamTopic
     * @param serviceId
     * @return
     */
    public Boolean isStreamTopicRegistered(String streamTopic, BezirkZirkId serviceId) {
        return isServiceRegistered(serviceId) && streamMap.containsKey(streamTopic) && streamMap.get(streamTopic).contains(serviceId);
    }

    /**
     * Checks for Discovery based on the Protocol Role and Location
     *
     * @param protocolRole
     * @param location
     * @return
     */
    public Set<BezirkDiscoveredZirk> discoverServices(ProtocolRole protocolRole, Location location) {
        if (!protocolMap.containsKey(protocolRole.getProtocolName())) {
            logger.debug("No services are subscribed for this protocol Role");
            return null;
        }
        final HashSet<BezirkDiscoveredZirk> discoveredServices = new HashSet<BezirkDiscoveredZirk>();
        // Get all the services associated with the protocols
        final Set<BezirkZirkId> services = protocolMap.get(protocolRole.getProtocolName());
        for (BezirkZirkId serviceId : services) {
            Location serviceLocation = getLocationForService(serviceId);

            if (null != location && !location.subsumes(serviceLocation)) {
                logger.debug("inside if before continue, Location did not match");
                continue;
            }
            discoveredServices.add(new BezirkDiscoveredZirk(
                    BezirkNetworkUtilities.getServiceEndPoint(serviceId), null, protocolRole, serviceLocation));
        }
        if (discoveredServices.isEmpty()) {
            logger.debug("No services are present in the location: {} subscribed to Protocol Role: {}",
                    location.toString(), protocolRole.getProtocolName());
            return null;
        }
        return discoveredServices;
    }


    /**
     * Checks the topic is registered by the recipient
     *
     * @param topic     topic needs to be checked
     * @param recipient recipient
     * @return true if registered, false otherwise
     */
    public boolean checkUnicastEvent(String topic, BezirkZirkId recipient) {
        return isServiceRegistered(recipient) && eventMap.containsKey(topic) && eventMap.get(topic).contains(recipient);
    }

    /**
     * Returns the Set<BezirkZirkId> associated with the Topic and Location
     *
     * @param topic    topic of the Event
     * @param location of the Zirk
     * @return Set<BezirkZirkId> if the zirks are present, <code>null</code> otherwise
     */
    public Set<BezirkZirkId> checkMulticastEvent(String topic, Location location) {
        HashSet<BezirkZirkId> services = null;

        if (eventMap.containsKey(topic)) {
            if (null == location) {
                return new HashSet<BezirkZirkId>(eventMap.get(topic));
            }

            HashSet<BezirkZirkId> tempServices = (HashSet<BezirkZirkId>) eventMap.get(topic);
            services = new HashSet<BezirkZirkId>();
            for (BezirkZirkId serviceId : tempServices) {
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
     * @return the list of Registered Zirk
     */
    public Set<BezirkZirkId> getRegisteredServices() {
        return new HashSet<BezirkZirkId>(sid);
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
