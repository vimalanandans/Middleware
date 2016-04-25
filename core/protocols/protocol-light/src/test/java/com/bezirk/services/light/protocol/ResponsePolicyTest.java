package com.bezirk.services.light.protocol;

import com.bezirk.services.light.protocol.HueVocab.Policy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the ResponsePolicy event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class ResponsePolicyTest {

    @Test
    public void test() {

        String location = "ROOM1";
        String king = "BOB";
        Policy policy = HueVocab.Policy.FCFS;
        ResponsePolicy responsePolicy = new ResponsePolicy(location, policy);
        responsePolicy.setKing(king);

        String serializedPolicy = responsePolicy.toJson();

        ResponsePolicy deserializedPolicy = ResponsePolicy.fromJson(serializedPolicy, ResponsePolicy.class);

        assertEquals("Policy not equal to the set policy.", policy, deserializedPolicy.getPolicy());
        assertEquals("Location not equal to the set location.", location, deserializedPolicy.getLocation());

        king = "FRED";
        location = "LAB";
        policy = HueVocab.Policy.KOH;
        responsePolicy = new ResponsePolicy(location, policy, king);
        responsePolicy.setLocation(location);
        responsePolicy.setPolicy(policy);
        Integer sensitivityToPresence = 50;
        responsePolicy.setSensitivityToPresence(sensitivityToPresence);
        serializedPolicy = responsePolicy.toJson();
        deserializedPolicy = ResponsePolicy.fromJson(serializedPolicy, ResponsePolicy.class);
        assertEquals("king not equal to the set value.", king, deserializedPolicy.getKing());
        assertEquals("Policy not equal to the set policy.", policy, deserializedPolicy.getPolicy());
        assertEquals("Location not equal to the set location.", location, deserializedPolicy.getLocation());
        assertEquals("SensitivityToPresence not equal to the set location.", sensitivityToPresence, deserializedPolicy.getSensitivityToPresence());


    }

}
