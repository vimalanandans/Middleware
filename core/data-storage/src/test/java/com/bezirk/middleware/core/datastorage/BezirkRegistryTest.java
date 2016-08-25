//package com.bezirk.middleware.core.persistence;
//
//import PubSubBrokerRegistry;
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
//        ProxyRegistry bezirkProxyRegistry = new ProxyRegistry();
//
//        PersistenceRegistry bezirkRegistry = new PersistenceRegistry();
//
//        bezirkRegistry.setId(id);
//        bezirkRegistry.setPubSubBrokerRegistry(pubSubBrokerRegistry);
//        bezirkRegistry.setSphereRegistry(sphereRegistry);
//        bezirkRegistry.setProxyRegistry(bezirkProxyRegistry);
//
//        assertEquals("ID is not equal to the set value.", id, bezirkRegistry.getId());
//        assertEquals("PubSubBrokerRegistry is not equal to the set value.", pubSubBrokerRegistry, bezirkRegistry.getPubSubBrokerRegistry());
//        assertEquals("SphererRegistry is not equal to the set value.", sphereRegistry, bezirkRegistry.getSphereRegistry());
//        assertEquals("ProxyRegistry is not equal to the set value.", bezirkProxyRegistry, bezirkRegistry.getProxyRegistry());
//    }
//
//}
