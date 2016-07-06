package com.bezirk.features;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Vimal S on 8/14/2015.
 * To test the feature test
 */
public class FeaturesTest {
    @Test
    public void TestBezirkComms() {
        /**
         * Make sure you have features.properties as per the feature implementation
         * features.properties should contain the below
         COMMS_BEZIRK=true
         COMMS_ZYRE=false
         TODO : use TogglzRule from http://www.togglz.org/documentation/testing.html
         * */
        for (com.bezirk.comms.CommsFeature feature : com.bezirk.comms.CommsFeature.values()) {

            System.out.println("Feature '%s' is active %s " + feature + " is " + feature.getValue() + " active : " + feature.isActive());

        }

        assertTrue("Bezirk comms should be enabled", com.bezirk.comms.CommsFeature.COMMS_BEZIRK.isActive());
        assertFalse("Bezirk zyre shouldn't be enabled", com.bezirk.comms.CommsFeature.COMMS_ZYRE.isActive());
        assertFalse("Bezirk secure should be null or not defined ", com.bezirk.comms.CommsFeature.COMMS_SECURE.isActive());
    }
}
