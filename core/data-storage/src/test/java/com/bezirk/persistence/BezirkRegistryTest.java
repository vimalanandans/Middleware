//package com.bezirk.persistence;
//
//import com.bezirk.pubsubbroker.PubSubBrokerRegistry;
//
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//
///**
// * @author ajc6kor
// */
//public class BezirkRegistryTest {
//
//    @Test
//    public void test() {
//
//        int id = 35;
//        PubSubBrokerRegistry pubSubBrokerRegistry = new PubSubBrokerRegistry();
//        SphereRegistry sphereRegistry = new SphereRegistry();
//        BezirkProxyRegistry bezirkProxyRegistry = new BezirkProxyRegistry();
//
//        BezirkRegistry bezirkRegistry = new BezirkRegistry();
//
//        bezirkRegistry.setId(id);
//        bezirkRegistry.setPubSubBrokerRegistry(pubSubBrokerRegistry);
//        bezirkRegistry.setSphereRegistry(sphereRegistry);
//        bezirkRegistry.setBezirkProxyRegistry(bezirkProxyRegistry);
//
//        assertEquals("ID is not equal to the set value.", id, bezirkRegistry.getId());
//        assertEquals("PubSubBrokerRegistry is not equal to the set value.", pubSubBrokerRegistry, bezirkRegistry.getPubSubBrokerRegistry());
//        assertEquals("SphererRegistry is not equal to the set value.", sphereRegistry, bezirkRegistry.getSphereRegistry());
//        assertEquals("BezirkProxyRegistry is not equal to the set value.", bezirkProxyRegistry, bezirkRegistry.getBezirkProxyRegistry());
//    }
//
//}
