package com.bezirk.persistence;

import com.bezirk.sadl.SadlRegistry;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author ajc6kor
 */
public class BezirkRegistryTest {

    @Test
    public void test() {

        int id = 35;
        SadlRegistry sadlRegistry = new SadlRegistry();
        SphereRegistry sphereRegistry = new SphereRegistry();
        UhuProxyRegistry uhuProxyRegistry = new UhuProxyRegistry();

        BezirkRegistry bezirkRegistry = new BezirkRegistry();

        bezirkRegistry.setId(id);
        bezirkRegistry.setSadlRegistry(sadlRegistry);
        bezirkRegistry.setSphereRegistry(sphereRegistry);
        bezirkRegistry.setUhuProxyRegistry(uhuProxyRegistry);

        assertEquals("ID is not equal to the set value.", id, bezirkRegistry.getId());
        assertEquals("SadlRegistry is not equal to the set value.", sadlRegistry, bezirkRegistry.getSadlRegistry());
        assertEquals("SphererRegistry is not equal to the set value.", sphereRegistry, bezirkRegistry.getSphereRegistry());
        assertEquals("UhuProxyRegistry is not equal to the set value.", uhuProxyRegistry, bezirkRegistry.getUhuProxyRegistry());
    }

}
