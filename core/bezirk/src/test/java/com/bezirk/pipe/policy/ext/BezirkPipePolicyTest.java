package com.bezirk.pipe.policy.ext;

import com.bezirk.middleware.addressing.PipePolicy;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the BezirkPipePolicy by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class BezirkPipePolicyTest {

    @Test
    public void test() {
        Map<String, String> reasonMap = new HashMap<>();
        String protocolName = "TestProtocol";
        String reason = "Used for testing";
        reasonMap.put(protocolName, reason);

        PipePolicy pipePolicy = new MockPipePolicy();
        pipePolicy.setReasonMap(reasonMap);
        BezirkPipePolicy bezirkPipePolicy = new BezirkPipePolicy(pipePolicy);

        String serializedUhuPipePolicy = bezirkPipePolicy.toJson();
        BezirkPipePolicy deserializedBezirkPipePolicy = BezirkPipePolicy.fromJson(serializedUhuPipePolicy, BezirkPipePolicy.class);

        assertTrue("TestProtocol is missing in the list of protocol names in uhupipepolicy.", deserializedBezirkPipePolicy.getProtocolNames().contains(protocolName));
        assertTrue("TestProtocol is missing in the list of allowed protocol names in uhupipepolicy.", deserializedBezirkPipePolicy.getAllowedProtocols().contains(protocolName));
        assertEquals("TestProtocol is missing in the list of protocol names in uhupipepolicy.", reasonMap, deserializedBezirkPipePolicy.getReasonMap());


        bezirkPipePolicy.authorize(protocolName);
        assertTrue("Authorized protocol is considered as unauthorized by uhupipepolicy.", bezirkPipePolicy.isAuthorized(protocolName));

        bezirkPipePolicy.unAuthorize(protocolName);
        assertFalse("Unauthorized protocol is considered as authorized by uhupipepolicy.", bezirkPipePolicy.isAuthorized(protocolName));


        String unknownProtocol = "InvalidProtocol";
        bezirkPipePolicy.authorize(unknownProtocol);
        assertFalse("BezirkPipePolicy is authorizing protocol which is not present in reasonmap.", bezirkPipePolicy.isAuthorized(protocolName));

        bezirkPipePolicy.unAuthorize(unknownProtocol);
        assertFalse("BezirkPipePolicy is unauthorizing protocol which is not present in reasonmap.", bezirkPipePolicy.isAuthorized(protocolName));
    }

    private class MockPipePolicy extends PipePolicy {
        public boolean isAuthorized(String protocolRoleName) {
            return false;
        }
    }
}
