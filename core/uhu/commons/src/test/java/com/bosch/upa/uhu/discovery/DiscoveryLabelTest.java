package com.bosch.upa.uhu.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * @author ajc6kor
 *
 */
public class DiscoveryLabelTest {

	@Test
	public void test() {

		int discoveryId =12;
		UhuServiceId uhuServiceId = new UhuServiceId("ServiceA");
		UhuServiceEndPoint requester = new UhuServiceEndPoint(uhuServiceId );
		requester.device= "DeviceA";
		DiscoveryLabel discoveryLabel = new DiscoveryLabel(requester, discoveryId);
	
		assertEquals("DiscoveryId is not equal to the set value.",discoveryId ,discoveryLabel.getDiscoveryId());
		assertEquals("Requester is not equal to the set value.",requester ,discoveryLabel.getRequester());
		
		DiscoveryLabel discoveryLabelTemp = new DiscoveryLabel(requester, discoveryId, true);
		assertTrue("SphereDiscovery is not equal to the set value.",discoveryLabelTemp.isSphereDiscovery());
		
		assertTrue("DiscoveryLabels with same requester and id are not considered equal",discoveryLabelTemp.equals(discoveryLabel));
		assertFalse("DiscoveryLabel is considered equal to requester.",discoveryLabelTemp.equals(requester));

		requester = new UhuServiceEndPoint(new UhuServiceId("ServiceB"));
		requester.device= "DeviceB";
		discoveryLabelTemp = new DiscoveryLabel(requester, discoveryId, true);
		assertFalse("DiscoveryLabels with different requester are considered equal",discoveryLabelTemp.equals(discoveryLabel));

		requester = new UhuServiceEndPoint(uhuServiceId);
		requester.device= "DeviceA";
		discoveryId=34;
		discoveryLabelTemp = new DiscoveryLabel(requester, discoveryId, true);
		assertFalse("DiscoveryLabels with different id are considered equal",discoveryLabelTemp.equals(discoveryLabel));

	}
	

}
