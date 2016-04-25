package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.services.light.protocol.HueVocab.Color;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the ResponseActiveLights events by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class ResponseActiveLightsTest {


    @Test
    public void test() {

        Set<LightDetails> lightDetails = new HashSet<>();
        LightDetails lightDetail = new LightDetails();
        lightDetail.setHubIp("121.0.0.0");
        lightDetail.setHubMac("TEST_MAC");
        lightDetail.setLightNumber(25);
        Color lightState = Color.BLUE;
        lightDetail.setLightState(lightState);

        lightDetails.add(lightDetail);
        Set<Integer> lightIds = new HashSet<Integer>();
        lightIds.add(25);
        lightIds.add(35);
        lightIds.add(45);
        Location location = new Location("FLOOR1/ROOM1/TABLE");

        ResponseActiveLights responseActiveLights = new ResponseActiveLights();
        responseActiveLights.setLightIdDetails(lightDetails);
        responseActiveLights.setLightIds(lightIds);
        responseActiveLights.setLocation(location);

        String serializedResponseActiveLights = responseActiveLights.toJSON();

        ResponseActiveLights deserializedResponseActiveLight = ResponseActiveLights.fromJSON(serializedResponseActiveLights, ResponseActiveLights.class);

        assertEquals("Location not equal to the set value.", location, deserializedResponseActiveLight.getLocation());
        assertEquals("LightDetails not equal to the set value.", lightDetails, deserializedResponseActiveLight.getLightIdDetails());
        assertEquals("LightIds not equal to the set value.", lightIds, deserializedResponseActiveLight.getLightIds());


    }

}
