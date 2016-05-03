package com.bezirk.services.light.protocol;

import com.bezirk.services.light.protocol.HueVocab.Color;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the LightDetails by setting the properties and retrieving them after deserialization.
 */
public class LightDetailsTest {


    @Test
    public void test() {

        String hubIp = "127.0.0.0";
        String hubMac = "127.0.0.0";
        Integer lightNumber = 25;
        Color lightState = Color.BLUE;
        LightDetails lightDetails = new LightDetails();
        lightDetails.setHubIp(hubIp);
        lightDetails.setHubMac(hubMac);
        lightDetails.setLightNumber(lightNumber);
        lightDetails.setLightState(lightState);

        assertEquals("HibIP is not equal to the set value.", hubIp, lightDetails.getHubIp());
        assertEquals("HubMac is not equal to the set value.", hubMac, lightDetails.getHubMac());
        assertEquals("LightNumber is not equal to the set value.", lightNumber, lightDetails.getLightNumber());
        assertEquals("LightState is not equal to the set value.", lightState, lightDetails.getLightState());


        //Test HashCode and Equals
        LightDetails lightDetailsTemp = new LightDetails();
        lightDetailsTemp.setHubIp(hubIp);
        lightDetailsTemp.setHubMac(hubMac);
        lightDetailsTemp.setLightNumber(lightNumber);
        lightDetailsTemp.setLightState(lightState);
        assertTrue("LightDetails with same properties are considered not equal", lightDetails.toString().equals(lightDetailsTemp.toString()));
        assertTrue("LightDetails with same HubIP and HubMac has different HashCode.", lightDetails.hashCode() == lightDetailsTemp.hashCode());
        assertTrue("LightDetails with same HubIP and HubMac are considered not equal", lightDetails.equals(lightDetailsTemp));

        assertTrue("LightDetails with same properties are not considered equal", lightDetails.equals(lightDetails));
        assertNotEquals("Null is considered equal to LightDetails.", null, lightDetails);
        assertFalse("TestString is considered equal to LightDetails.", lightDetails.equals("TEST"));

        lightDetailsTemp.setHubIp("127.1.1.1");
        assertFalse("LightDetails with different HubIP and HubMac has same HashCode.", lightDetails.hashCode() == lightDetailsTemp.hashCode());
        assertFalse("LightDetails with different properties are considered equal", lightDetails.equals(lightDetailsTemp));

        lightDetailsTemp.setHubIp("127.0.0.0");
        lightDetailsTemp.setHubMac("127.1.1.1");
        assertFalse("LightDetails with different HubIP and HubMac has same HashCode.", lightDetails.hashCode() == lightDetailsTemp.hashCode());
        assertFalse("LightDetails with different properties are considered equal", lightDetails.equals(lightDetailsTemp));

        lightDetailsTemp.setHubIp(null);
        assertFalse("LightDetails with different LightNumber,HubIP and HubMac has same HashCode.", lightDetailsTemp.hashCode() == lightDetails.hashCode());
        assertFalse("LightDetails with different properties are considered equal.", lightDetailsTemp.equals(lightDetails) && lightDetails.equals(lightDetailsTemp));

        lightDetailsTemp.setHubIp("127.0.0.0");
        lightDetailsTemp.setHubMac(null);
        assertFalse("LightDetails with different LightNumber,HubIP and HubMac has same HashCode.", lightDetailsTemp.hashCode() == lightDetails.hashCode());
        assertFalse("LightDetails with different properties are considered equal.", lightDetailsTemp.equals(lightDetails) && lightDetails.equals(lightDetailsTemp));

        lightDetailsTemp.setHubMac("127.0.0.0");
        lightDetailsTemp.setLightNumber(null);
        assertFalse("LightDetails with different LightNumber,HubIP and HubMac has same HashCode.", lightDetailsTemp.hashCode() == lightDetails.hashCode());
        assertFalse("LightDetails with different properties are considered equal.", lightDetailsTemp.equals(lightDetails) && lightDetails.equals(lightDetailsTemp));

        lightDetailsTemp.setLightNumber(35);
        assertFalse("LightDetails with different LightNumber,HubIP and HubMac has same HashCode.", lightDetailsTemp.hashCode() == lightDetails.hashCode());
        assertFalse("LightDetails with different properties are considered equal.", lightDetailsTemp.equals(lightDetails) && lightDetails.equals(lightDetailsTemp));

        lightDetailsTemp.setHubIp(null);
        lightDetailsTemp.setHubMac(null);
        lightDetailsTemp.setLightNumber(null);
        lightDetails.setHubIp(null);
        lightDetails.setHubMac(null);
        lightDetails.setLightNumber(null);
        assertTrue("LightDetails with same properties are not considered equal.", lightDetailsTemp.equals(lightDetails) && lightDetails.equals(lightDetailsTemp));
    }

}
