package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.services.light.protocol.HueVocab.Color;
import com.bezirk.services.light.protocol.HueVocab.Commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the BulbStatus event by setting the properties and retrieving them after deserialization.
 */
public class BulbStatusTest {


    @Test
    public void test() {

        Integer lightNumber = 25;
        Commands command = Commands.TOGGLE;
        BulbStatus bulbStatus = new BulbStatus(lightNumber, command);

        String serializedBulbStatus = bulbStatus.toJson();

        BulbStatus deserializedBulbStatus = BulbStatus.fromJson(serializedBulbStatus, BulbStatus.class);
        assertEquals("LightNumber is not equal to the set value.", lightNumber, deserializedBulbStatus.getLightNumber());
        assertEquals("Command is not equal to the set value.", command, deserializedBulbStatus.getCommand());
        assertEquals("Color is not equal to the default value.", HueVocab.Color.DEFAULT, deserializedBulbStatus.getColor());
        assertTrue("Bulb Status by ID is false.", deserializedBulbStatus.getById());

        Location location = new Location("FLOOR1/ROOM1/TABLE");
        bulbStatus = new BulbStatus(location, command);
        Color color = Color.PINK;
        bulbStatus.setColor(color);
        serializedBulbStatus = bulbStatus.toJson();

        deserializedBulbStatus = BulbStatus.fromJson(serializedBulbStatus, BulbStatus.class);
        assertEquals("Location is not equal to the set value.", location, deserializedBulbStatus.getLocation());
        assertFalse("Bulb Status by ID is false.", deserializedBulbStatus.getById());
        assertEquals("Color is not equal to the set value.", color, deserializedBulbStatus.getColor());

    }

}
