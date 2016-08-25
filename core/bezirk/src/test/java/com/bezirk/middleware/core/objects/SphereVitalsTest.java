package com.bezirk.middleware.core.objects;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the SphereVitals by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */
public class SphereVitalsTest {

    @Test
    public void test() {

        byte[] sphereKey = "tdgdiknvx".getBytes();
        byte[] publicKey = "oihyurgbd".getBytes();
        com.bezirk.middleware.objects.SphereVitals sphereVitals = new com.bezirk.middleware.objects.SphereVitals(sphereKey, publicKey);

        assertArrayEquals("SphereKey is not equal to the set value.", sphereKey, sphereVitals.getSphereKey());
        assertArrayEquals("PublicKey is not equal to the set value.", publicKey, sphereVitals.getPublicKey());

    }

}