package com.bezirk.persistence;

import com.bezirk.sadl.SadlRegistry;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author ajc6kor
 */
public class UhuRegistryTest {

    @Test
    public void test() {

        int id = 35;
        SadlRegistry sadlRegistry = new SadlRegistry();
        SphereRegistry sphereRegistry = new SphereRegistry();
        UhuProxyRegistry uhuProxyRegistry = new UhuProxyRegistry();

        UhuRegistry uhuRegistry = new UhuRegistry();

        uhuRegistry.setId(id);
        uhuRegistry.setSadlRegistry(sadlRegistry);
        uhuRegistry.setSphereRegistry(sphereRegistry);
        uhuRegistry.setUhuProxyRegistry(uhuProxyRegistry);

        assertEquals("ID is not equal to the set value.", id, uhuRegistry.getId());
        assertEquals("SadlRegistry is not equal to the set value.", sadlRegistry, uhuRegistry.getSadlRegistry());
        assertEquals("SphererRegistry is not equal to the set value.", sphereRegistry, uhuRegistry.getSphereRegistry());
        assertEquals("UhuProxyRegistry is not equal to the set value.", uhuProxyRegistry, uhuRegistry.getUhuProxyRegistry());
    }

}
