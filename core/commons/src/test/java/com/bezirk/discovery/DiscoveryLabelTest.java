package com.bezirk.discovery;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author ajc6kor
 */
public class DiscoveryLabelTest {

    @Test
    public void test() {

        int discoveryId = 12;
        ZirkId zirkId = new ZirkId("ZirkA");
        BezirkZirkEndPoint requester = new BezirkZirkEndPoint(zirkId);
        requester.device = "DeviceA";
        com.bezirk.pubsubbroker.discovery.DiscoveryLabel discoveryLabel = new com.bezirk.pubsubbroker.discovery.DiscoveryLabel(requester, discoveryId);

        assertEquals("DiscoveryId is not equal to the set value.", discoveryId, discoveryLabel.getDiscoveryId());
        assertEquals("Requester is not equal to the set value.", requester, discoveryLabel.getRequester());

        com.bezirk.pubsubbroker.discovery.DiscoveryLabel discoveryLabelTemp = new com.bezirk.pubsubbroker.discovery.DiscoveryLabel(requester, discoveryId, true);
        assertTrue("SphereDiscovery is not equal to the set value.", discoveryLabelTemp.isSphereDiscovery());

        assertTrue("DiscoveryLabels with same requester and id are not considered equal", discoveryLabelTemp.equals(discoveryLabel));
        assertFalse("DiscoveryLabel is considered equal to requester.", discoveryLabelTemp.equals(requester));

        requester = new BezirkZirkEndPoint(new ZirkId("ServiceB"));
        requester.device = "DeviceB";
        discoveryLabelTemp = new com.bezirk.pubsubbroker.discovery.DiscoveryLabel(requester, discoveryId, true);
        assertFalse("DiscoveryLabels with different requester are considered equal", discoveryLabelTemp.equals(discoveryLabel));

        requester = new BezirkZirkEndPoint(zirkId);
        requester.device = "DeviceA";
        discoveryId = 34;
        discoveryLabelTemp = new com.bezirk.pubsubbroker.discovery.DiscoveryLabel(requester, discoveryId, true);
        assertFalse("DiscoveryLabels with different id are considered equal", discoveryLabelTemp.equals(discoveryLabel));

    }


}
