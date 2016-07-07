//package com.bezirk.test.pipes;
//
//import com.bezirk.middleware.addressing.PipePolicy;
//import com.bezirk.middleware.messages.ProtocolRole;
//import com.bezirk.protocols.penguin.v01.UserProxyRole;
//import com.bezirk.protocols.smartservice.SmartServiceRole;
//
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
///**
// * @author Mansi
// */
//public class PipePolicyTest {
//    private final String reason1 = "For test 1 protocol";
//    private final String reason2 = "For test 2 protocol";
//    private final ProtocolRole pRole1 = new UserProxyRole();
//    private final ProtocolRole pRole2 = new SmartServiceRole();
//
//    @Test
//    public void test() {
//        PipePolicy policy = new MockPipePolicy();
//        policy.addAllowedProtocol(pRole1, reason1);
//        policy.addAllowedProtocol(pRole2, reason2);
//        String sPolicy = policy.toJson();
//        System.out.println("Policy try out: " + sPolicy);
//        PipePolicy deserializedPolicy = PipePolicy.fromJson(sPolicy, MockPipePolicy.class);
//        assertTrue(deserializedPolicy.getReason(pRole1.getRoleName()).equals(reason1));
//        assertTrue(deserializedPolicy.getReason(pRole2.getRoleName()).equals(reason2));
//    }
//
//    private class MockPipePolicy extends PipePolicy {
//        public boolean isAuthorized(String protocolRoleName) {
//            return false;
//        }
//    }
//}
