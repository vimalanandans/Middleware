package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.services.light.protocol.HueVocab.Color;
import com.bezirk.services.light.protocol.HueVocab.Commands;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the ActuateBulb event by setting the properties and retrieving them after deserialization.
 */
public class ActuateBulbTest {

    @Test
    public void test() {

        Set<Integer> lightNumber = new HashSet<>();
        lightNumber.add(25);
        Location location = new Location("FLOOR1/ROOM1/TABLE");
        Commands command = Commands.ON;

        ActuateBulb actuateBulb = new ActuateBulb(location, command);
        actuateBulb.setLightNumber(lightNumber);
        String serializedActuateBulb = actuateBulb.toJson();

        ActuateBulb deserializedActuateBulb = ActuateBulb.fromJson(serializedActuateBulb, ActuateBulb.class);

        assertEquals("Command is not equal to the set value.", command, deserializedActuateBulb.getCommand());
        assertEquals("Location is not equal to the set value.", location, deserializedActuateBulb.getLocation());
        assertEquals("LightNumber is not equal to the set value.", lightNumber, deserializedActuateBulb.getLightNumber());


        command = Commands.OFF;
        Color color = Color.BLUE;
        String hubIP = "127.0.0.0";
        String hubMac = "127.0.0.0";
        actuateBulb = new ActuateBulb(lightNumber, command);
        actuateBulb.setColor(color);
        actuateBulb.setHubIP(hubIP);
        actuateBulb.setHubMac(hubMac);

        serializedActuateBulb = actuateBulb.toJson();

        deserializedActuateBulb = ActuateBulb.fromJson(serializedActuateBulb, ActuateBulb.class);
        assertEquals("Command is not equal to the set value.", command, deserializedActuateBulb.getCommand());
        assertEquals("LightNumber is not equal to the set value.", lightNumber, deserializedActuateBulb.getLightNumber());
        assertEquals("HubIP is not equal to the set value.", hubIP, deserializedActuateBulb.getHubIP());
        assertEquals("HubMap is not equal to the set value.", hubMac, deserializedActuateBulb.getHubMac());
        assertEquals("Color is not equal to the set value.", color, deserializedActuateBulb.getColor());


    }

}
