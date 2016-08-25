package com.bezirk.middleware.core.pubsubbroker;

import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

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
     * Stores the list of Registered ZirkId's of the Zirks.
     */
    protected final Set<ZirkId> zid = new HashSet<>();
    /**
     * Stores the list of Zirks associated with MessageSets. Typically used in Discovery.
     * [Key -&gt; Value] = [MessageSet -&gt; [Set of ZirkIds]]
     */
    protected final Map<MessageSet, Set<ZirkId>> subscriptionMap = new ConcurrentHashMap<>();
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
    /**
     * Default location used when a new zirk registers. Also used to check during location matching
     */
    private final Location defaultLocation = new Location(null, null, null);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = checkNullAndComputeHashCode(prime, result, defaultLocation);
        result = checkNullAndComputeHashCode(prime, result, eventMap);
        result = checkNullAndComputeHashCode(prime, result, locationMap);
        result = checkNullAndComputeHashCode(prime, result, logger);
        result = checkNullAndComputeHashCode(prime, result, subscriptionMap);
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
                "protocolDescMap", "subscriptionMap", "streamMap");

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
    public Boolean registerZirk(final ZirkId zirkId) {

        zid.add(zirkId);
        // Update the location!
        setLocation(zirkId, defaultLocation);
        logger.debug(zirkId + " is registered successfully");
        return true;
    }

    private Map<String, Set<ZirkId>> getMessageMap(MessageSet messageSet) {
        final Map<String, Set<ZirkId>> messageMap;

        if (messageSet instanceof EventSet) {
            messageMap = eventMap;
        } else if (messageSet instanceof StreamSet) {
            messageMap = streamMap;
        } else {
            throw new AssertionError("Unknown MessageSet type: " +
                    messageSet.getClass().getSimpleName());
        }

        return messageMap;
    }

    /**
     * Subscribes the Zirk with the Protocol Role.
     *
     * @param zirkId     of the zirk being subscribed
     * @param messageSet protocolRole of the zirk.
     * @return true if subscribed, false otherwise
     */
    public Boolean subscribe(final ZirkId zirkId, final MessageSet messageSet) {
        // Update the Protocol Map
        final Set<ZirkId> protocolZirks;
        if (subscriptionMap.containsKey(messageSet)) {
            protocolZirks = subscriptionMap.get(messageSet);
            protocolZirks.add(zirkId);
        } else {
            protocolZirks = new HashSet<>();
            protocolZirks.add(zirkId);
            subscriptionMap.put(messageSet, protocolZirks);
        }

        final Map<String, Set<ZirkId>> messageMap = getMessageMap(messageSet);

        for (String eventName : messageSet.getMessages()) {
            final Set<ZirkId> eventsResZirks;
            if (messageMap.containsKey(eventName)) {
                eventsResZirks = messageMap.get(eventName);
            } else {
                eventsResZirks = new HashSet<>();
            }

            eventsResZirks.add(zirkId);
            messageMap.put(eventName, eventsResZirks);
        }

        return true;
    }

    /**
     * Un subscribes the zirk for the protocolRole
     *
     * @param zirkId     ZirkId of the zirk being unsubscribed
     * @param messageSet MessageSet of the zirk being unsubscribed
     * @return true if unsubscribed, false otherwise
     */
    public Boolean unsubscribe(final ZirkId zirkId, final MessageSet messageSet) {
        if (subscriptionMap.containsKey(messageSet)) {
            final Set<ZirkId> zirkIdSet = subscriptionMap.get(messageSet);

            if (!zirkIdSet.remove(zirkId)) {
                logger.debug("Zirk is Trying to unsubscribe that it has not subscribed to");
                return false;
            }
            // Remove from Protocol map
            if (zirkIdSet.isEmpty()) {
                subscriptionMap.remove(messageSet);
            } else {
                subscriptionMap.put(messageSet, zirkIdSet);
            }

            final Map<String, Set<ZirkId>> messageMap = getMessageMap(messageSet);

            for (String messageName : messageSet.getMessages()) {
                removeMessageFromMap(messageName, zirkId, messageMap);
            }

            return true;
        }

        return false;
    }

    public Boolean unregisterZirk(final ZirkId zirkId) {
        if (isZirkRegistered(zirkId)) {
            // Remove the events from event Map
            removeZidFromMessageMaps(zirkId, eventMap);
            // remove the steams from stream map
            removeZidFromMessageMaps(zirkId, streamMap);
            // remove the protocol from protocol Map
            removeZidFromSubscriptionMap(zirkId, subscriptionMap);
            // Remove the Location
            locationMap.remove(zirkId);
            //remove Sid
            zid.remove(zirkId);
            return true;
        }
        logger.debug("Zirk tried to Unregister that does not exist");
        return false;
    }

    /**
     * Update the location of the Zirk
     *
     * @param zirkId   ZirkId of the zirk
     * @param location Location of the zirk
     * @return true if updated, false otherwise
     */
    public Boolean setLocation(final ZirkId zirkId, final Location location) {
        if (isZirkRegistered(zirkId)) {
            locationMap.put(zirkId, location);
            return true;
        }
        logger.debug("Tried to update the location for the Zirk that is not subscribed");
        return false;
    }

    /**
     * Checks if ZirkId is registered
     *
     * @param zirkId of the Zirk
     * @return true if zid contains ZirkId, false otherwise
     */
    public Boolean isZirkRegistered(ZirkId zirkId) {
        return zid.contains(zirkId);
    }

    /**
     * Returns latest location of the zirk
     *
     * @param zirkId whose location needs to be known
     * @return Location of the zirk
     */
    public Location getLocationForZirk(ZirkId zirkId, Device device) {
        if (isZirkRegistered(zirkId)) {
            try {
                return (defaultLocation.equals(locationMap.get(zirkId)) ? device.getDeviceLocation() : locationMap.get(zirkId));
            } catch (Exception e) {
                logger.error("Exception in fetching the device Location", e);
                return null;
            }
        }
        logger.error("Zirk Tried to fetch the location that is not Registered");
        return null;
    }

    private void removeMessageFromMap(final String messageName, final ZirkId zirkId,
                                      final Map<String, Set<ZirkId>> messageMap) {
        if (messageMap.containsKey(messageName)) {
            final Set<ZirkId> zirkIdSetEvents = messageMap.get(messageName);

            if (zirkIdSetEvents.contains(zirkId)) {
                zirkIdSetEvents.remove(zirkId);
                if (zirkIdSetEvents.isEmpty()) {
                    messageMap.remove(messageName);
                } else {
                    messageMap.put(messageName, zirkIdSetEvents);
                }
            }
        }
    }

    private void removeZidFromMessageMaps(final ZirkId zirkId, final Map<String, Set<ZirkId>> messageMap) {
        for (Entry<String, Set<ZirkId>> entry : messageMap.entrySet()) {
            final Set<ZirkId> zirkIdSet = entry.getValue();

            if (zirkIdSet.contains(zirkId)) {
                zirkIdSet.remove(zirkId);
                if (zirkIdSet.isEmpty()) {
                    messageMap.remove(entry.getKey());
                }
            }
        }
    }

    private void removeZidFromSubscriptionMap(final ZirkId zirkId, final Map<MessageSet, Set<ZirkId>> subscriptionMap) {
        for (Entry<MessageSet, Set<ZirkId>> entry : subscriptionMap.entrySet()) {
            final Set<ZirkId> zirkIdSet = entry.getValue();

            if (zirkIdSet.contains(zirkId)) {
                zirkIdSet.remove(zirkId);
                if (zirkIdSet.isEmpty()) {
                    subscriptionMap.remove(entry.getKey());
                }
            }
        }
    }

    public Boolean isStreamTopicRegistered(String streamName, ZirkId zirkId) {
        return isZirkRegistered(zirkId) && streamMap.containsKey(streamName) &&
                streamMap.get(streamName).contains(zirkId);
    }

    /**
     * Checks the topic is registered by the recipient
     *
     * @param eventName topic needs to be checked
     * @param recipient recipient
     * @return true if registered, false otherwise
     */
    public boolean checkUnicastEvent(String eventName, ZirkId recipient) {
        return isZirkRegistered(recipient) && eventMap.containsKey(eventName) &&
                eventMap.get(eventName).contains(recipient);
    }

    /**
     * Returns the Set&lt;ZirkId&gt; associated with the Topic and Location
     *
     * @param eventName topic of the Event
     * @param location  of the Zirk
     * @return Set&lt;ZirkId&gt; if the zirks are present, <code>null</code> otherwise
     */
    public Set<ZirkId> checkMulticastEvent(String eventName, Location location, Device device) {
        Set<ZirkId> zirks = null;

        if (eventMap.containsKey(eventName)) {
            if (null == location) {
                return new HashSet<>(eventMap.get(eventName));
            }

            Set<ZirkId> tempZirks = eventMap.get(eventName);
            zirks = new HashSet<>();
            for (ZirkId zirkId : tempZirks) {
                Location zirkLocation = getLocationForZirk(zirkId, device);
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
     * Get the list of Registered Zirks
     *
     * @return the list of Registered Zirk
     */
    public Set<ZirkId> getRegisteredZirks() {
        return new HashSet<>(zid);
    }

    /**
     * Clears all the registryclone
     */
    public void clearRegistry() {
        this.zid.clear();
        this.subscriptionMap.clear();
        this.eventMap.clear();
        this.streamMap.clear();
        this.locationMap.clear();
    }
}
