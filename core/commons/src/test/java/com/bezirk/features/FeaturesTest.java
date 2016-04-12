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
    public void TestUhuComms()
    {
        /**
         * Make sure you have features.properties as per the feature implementation
         * features.properties should contain the below
            COMMS_UHU=true
            COMMS_ZYRE=false
            TODO : use TogglzRule from http://www.togglz.org/documentation/testing.html
         * */
        for ( CommsFeature feature : CommsFeature.values() ) {

            System.out.println(  "Feature '%s' is active %s "+ feature+" is "+ feature.getValue() + " active : "+feature.isActive());

        }

        assertTrue("Uhu comms should be enabled", CommsFeature.COMMS_UHU.isActive());
        assertFalse("Uhu zyre shouldn't be enabled", CommsFeature.COMMS_ZYRE.isActive());
        assertFalse("Uhu secure should be null or not defined ", CommsFeature.COMMS_SECURE.isActive());
    }
}
