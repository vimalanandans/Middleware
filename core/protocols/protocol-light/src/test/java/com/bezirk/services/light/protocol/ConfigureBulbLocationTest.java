package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the ConfigureBulbLocation event by setting the properties and retrieving them after deserialization.
 */
public class ConfigureBulbLocationTest {


    @Test
    public void test() {

        Integer id = 25;
        Location location = new Location("FLOOR1/ROOM1/TABLE");
        ConfigureBulbLocation configBulbLocation = new ConfigureBulbLocation(id, location);

        String serializedConfigBulbLocation = configBulbLocation.toJson();

        ConfigureBulbLocation deserializedConfigBulbLocation = ConfigureBulbLocation.fromJson(serializedConfigBulbLocation, ConfigureBulbLocation.class);

        assertEquals("ID is not equal to the set value.", id, deserializedConfigBulbLocation.getId());
        assertEquals("Location is not equal to the set value.", location, deserializedConfigBulbLocation.getLocation());


    }

}