package com.bezirk.services.light.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the RequestLightLocation event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class RequestLightLocationTest {

    @Test
    public void test() {

        Integer id = 1234;
        RequestLightLocation requestLightLocation = new RequestLightLocation(id);

        String serializedRequestLightLocation = requestLightLocation.serialize();

        RequestLightLocation deserializedRequestLightLocation = RequestLightLocation.deserialize(serializedRequestLightLocation, RequestLightLocation.class);

        assertEquals("LightID is different from the set value.", id, deserializedRequestLightLocation.getLightId());

    }

}
