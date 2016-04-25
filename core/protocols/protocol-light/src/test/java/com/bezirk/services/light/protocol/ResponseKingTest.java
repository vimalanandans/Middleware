package com.bezirk.services.light.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the ResponseKing event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class ResponseKingTest {


    @Test
    public void test() {

        String location = "FLOOR1/ROOM1/TABLE";
        String king = "BOB";
        ResponseKing responseKing = new ResponseKing(location, king);

        String serializedResponseKing = responseKing.toJson();

        ResponseKing deserializedResponseKing = ResponseKing.fromJson(serializedResponseKing, ResponseKing.class);

        assertEquals("King is not equal to the set value.", king, deserializedResponseKing.getKing());
        assertEquals("Location is not equal to the set value.", location, deserializedResponseKing.getLocation());


    }

}
