package com.bezirk.pubsubbroker;

import com.bezirk.devices.DeviceInterface;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.ZirkId;


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

/**
 * Registry Class that deals with all the maps
 */
public class PubSubBrokerRegistry implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(PubSubBrokerRegistry.class);
    /**
     * Default location used when a new zirk registers. Also used to check during location matching
     */
    private final Location defaultLocation = new Location(null, null, null);
    /**
     * Stores the list of Registered ZirkId's of the Services.
     */
    protected final Set<ZirkId> zid = new HashSet<>();
    /**
     * Stores the list of Services associated with the Protocol. Typically used in Discovery.
     * [Key -&gt; Value] = [ProtocolName -&gt; [Set of ServiceIds]]
     */
    protected final Map<String, Set<ZirkId>> protocolMap = new ConcurrentHashMap<>();
    /**
     * Stores the Protocol Description associated with the ProtocolRole. Typically used in Discovery.
     * [Key -&gt; Value] = [ProtocolName -&gt; Protocol description]
     */
    protected final Map<String, String> protocolDescMap = new ConcurrentHashMap<>();
    /**
     * Stores the zirkIds mapped to the event topics. Typically used in Sending/ Receiving Event.
     * [Key -&gt; Value] = [eventTopic -&gt; [Set of ZirkIds]]
     */
    protected final Map<String, Set<ZirkId>> eventMap = new ConcurrentHashMap<>();
    /**
     * Stores the ZirkIds mapped to the stream topics. Typically used in Streaming.
     * [Key -&gt; Value] = [streamTopic -&gt; [Set of ZirkIds]]
     */
    protected final Map<String, Set<ZirkId>> streamMap = new ConcurrentHashMap<>();
    /**
     * Stores the location of the Zirkss.
     * [Key -&gt; Value] = [ZirkId -&gt; Location]
     */
    protected final Map<ZirkId, Location> locationMap = new ConcurrentHashMap<>();

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
        result = checkNullAndComputeHashCode(prime, result, zid);
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
        PubSubBrokerRegistry other = (PubSubBrokerRegistry) obj;
        return checkMaps(other) && defaultLocation.equals(other.defaultLocation);
    }

    private boolean checkMaps(PubSubBrokerRegistry other) {

        List<String> mapList = Arrays.asList("eventMap", "locationMap",
                "protocolDescMap", "protocolMap", "streamMap");

        for (String map : mapList) {

            Field field;
            try {
                field = PubSubBrokerRegistry.class.getDeclaredField(map);

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

                logger.error("Unable to retrieve maps from pubSubBrokerRegistry for checking.", e);

                return false;
            }
        }
        return true;
    }

    /**
     * Registers a Zirk
     *
     * @param zirkId of the zirk being registered
     * @return true if registered, false otherwise
     */
    public Boolean registerService(final ZirkId zirkId) {

        zid.add(zirkId);
        // Update the location!
        setLocation(zirkId, defaultLocation);
        logger.info(zirkId + " is registered successfully");
        return true;
    }


    /**
     * Subscribes the Zirk with the Protocol Role.
     *
     * @param zirkId of the zirk being subscribed
     * @param pRole     protocolRole of the zirk.
     * @return true if subscribed, false otherwise
     */
    public Boolean subscribeService(final ZirkId zirkId, final ProtocolRole pRole) {
        final String protocolName = pRole.getRoleName();
        final String protocolDescription = pRole.getDescription();
        final String[] eventsTopics = pRole.getEventTopics();
        final String[] streamTopics = pRole.getStreamTopics();

        // Update the Protocol Map
        final Set<ZirkId> protocolServices;
        if (protocolMap.containsKey(protocolName)) {
            protocolServices = protocolMap.get(protocolName);
            protocolServices.add(zirkId);
        } else {
            protocolServices = new HashSet<>();
            protocolServices.add(zirkId);
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
                final Set<ZirkId> eventsResServices;
                if (eventMap.containsKey(eventTopic)) {
                    eventsResServices = eventMap.get(eventTopic);
                } else {
                    eventsResServices = new HashSet<>();
                }

                eventsResServices.add(zirkId);
                eventMap.put(eventTopic, eventsResServices);
            }
        }
        // Updating StreamDescriptor Map
        if (null == streamTopics) {
            logger.info("Protocol does not contain any Streams to subscribe");
        } else {
            for (String streamTopic : streamTopics) {
                final Set<ZirkId> strmsResServices;
                if (streamMap.containsKey(streamTopic)) {
                    strmsResServices = streamMap.get(streamTopic);
                } else {
                    strmsResServices = new HashSet<>();
                }

                strmsResServices.add(zirkId);
                streamMap.put(streamTopic, strmsResServices);
            }
        }
        logger.info(protocolName + " Protocol Role subscribed successfully");
        return true;
    }


    /**
     * Un subscribes the zirk for the protocolRole
     *
     * @param zirkId ZirkId of the zirk being unsubscribed
     * @param role      protocolRole of the zirk being unsubscribed
     * @return true if unsubscribed, false otherwise
     */
    public Boolean unsubscribe(final ZirkId zirkId, final ProtocolRole role) {
        if (protocolMap.containsKey(role.getRoleName())) {
            final Set<ZirkId> zirkIdSet = protocolMap.get(role.getRoleName());

            if (!zirkIdSet.remove(zirkId)) {
                logger.info("Zirk is Trying to unsubscribe that it has not subscribed to");
                return false;
            }
            // Remove from Protocol map
            if (zirkIdSet.isEmpty()) {
                protocolMap.remove(role.getRoleName());
                // Remove the Description
                protocolDescMap.remove(role.getRoleName());
            } else {
                protocolMap.put(role.getRoleName(), zirkIdSet);
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
        logger.info(zirkId + "Zirk tried to Unsubscribe  " + role.getRoleName() + " without Registration/ Zirk might be already unsubscribed");
        return false;
    }

    public Boolean unregisterZirk(final ZirkId zirkId) {
        if (isServiceRegistered(zirkId)) {
            // Remove the events from event Map
            removeZidFromMaps(zirkId, eventMap, false);
            // remove the steams from stream map
            removeZidFromMaps(zirkId, streamMap, false);
            // remove the protocol from protocol Map
            removeZidFromMaps(zirkId, protocolMap, true);
            // Remove the Location
            locationMap.remove(zirkId);
            //remove Sid
            zid.remove(zirkId);
            return true;
        }
        logger.info("Zirk tried to Unregister that does not exist");
        return false;
    }

    /**
     * Update the location of the Zirk
     *
     * @param zirkId ZirkId of the zirk
     * @param location  Location of the zirk
     * @return true if updated, false otherwise
     */
    public Boolean setLocation(final ZirkId zirkId, final Location location) {
        if (isServiceRegistered(zirkId)) {
            locationMap.put(zirkId, location);
            return true;
        }
        logger.info("Tried to update the location for the Zirk that is not subscribed");
        return false;
    }

    /**
     * Checks if ZirkId is registered
     *
     * @param zirkId of the Zirk
     * @return true if zid contains ZirkId, false otherwise
     */
    public Boolean isServiceRegistered(ZirkId zirkId) {
        return zid.contains(zirkId);
    }

    /**
     * Returns latest location of the zirk
     *
     * @param zirkId whose location needs to be known
     * @return Location of the zirk
     */
    public Location getLocationForService(ZirkId zirkId, DeviceInterface deviceInterface) {
        if (isServiceRegistered(zirkId)) {
            try {
                return (defaultLocation.equals(locationMap.get(zirkId)) ? deviceInterface.getDeviceLocation() : locationMap.get(zirkId));
            } catch (Exception e) {
                logger.error("Exception in fetching the device Location", e);
                return null;
            }
        }
        logger.error("Zirk Tried to fetch the location that is not Registered");
        return null;
    }

    private void removeTopicFromMap(final String topic, final ZirkId zirkId, final Map<String, Set<ZirkId>> eventMap2) {
        if (eventMap2.containsKey(topic)) {
            HashSet<ZirkId> zirkIdSetEvents = (HashSet<ZirkId>) eventMap2.get(topic);
            if (zirkIdSetEvents.contains(zirkId)) {
                zirkIdSetEvents.remove(zirkId);
                if (zirkIdSetEvents.isEmpty()) {
                    eventMap2.remove(topic);
                } else {
                    eventMap2.put(topic, zirkIdSetEvents);
                }
            }
        }
    }

    private void removeZidFromMaps(final ZirkId zirkId, final Map<String, Set<ZirkId>> eventMap2, final boolean isProtocol) {
        for (Entry<String, Set<ZirkId>> entry : eventMap2.entrySet()) {
            if (entry.getValue().contains(zirkId)) {
                entry.getValue().remove(zirkId);
                if (entry.getValue().isEmpty()) {
                    eventMap2.remove(entry.getKey());
                    if (isProtocol) {
                        protocolDescMap.remove(entry.getKey());
                    }
                }
            }
        }
    }

    public Boolean isStreamTopicRegistered(String streamTopic, ZirkId zirkId) {
        return isServiceRegistered(zirkId) && streamMap.containsKey(streamTopic) && streamMap.get(streamTopic).contains(zirkId);
    }

    /**
     * Checks the topic is registered by the recipient
     *
     * @param topic     topic needs to be checked
     * @param recipient recipient
     * @return true if registered, false otherwise
     */
    public boolean checkUnicastEvent(String topic, ZirkId recipient) {
        return isServiceRegistered(recipient) && eventMap.containsKey(topic) && eventMap.get(topic).contains(recipient);
    }

    /**
     * Returns the Set&lt;ZirkId&gt; associated with the Topic and Location
     *
     * @param topic    topic of the Event
     * @param location of the Zirk
     * @return Set&lt;ZirkId&gt; if the zirks are present, <code>null</code> otherwise
     */
    public Set<ZirkId> checkMulticastEvent(String topic, Location location, DeviceInterface deviceInterface) {
        Set<ZirkId> zirks = null;

        if (eventMap.containsKey(topic)) {
            if (null == location) {
                return new HashSet<>(eventMap.get(topic));
            }

            HashSet<ZirkId> tempServices = (HashSet<ZirkId>) eventMap.get(topic);
            zirks = new HashSet<>();
            for (ZirkId zirkId : tempServices) {
                Location zirkLocation = getLocationForService(zirkId, deviceInterface);
                if (zirkLocation != null && location.subsumes(zirkLocation)) {
                    zirks.add(zirkId);
                }
            }
            if (zirks.isEmpty()) {
                return null;
            }
        }
        return zirks;
    }

    /**
     * Get the list of Registered Services
     *
     * @return the list of Registered Zirk
     */
    public Set<ZirkId> getRegisteredServices() {
        return new HashSet<>(zid);
    }

    /**
     * Clears all the registryclone
     */
    public void clearRegistry() {
        this.zid.clear();
        this.protocolMap.clear();
        this.protocolDescMap.clear();
        this.eventMap.clear();
        this.streamMap.clear();
        this.locationMap.clear();
    }
}
