package com.bezirk.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.api.objects.UhuSphereInfo;
import com.bezirk.control.messages.discovery.SphereDiscoveryResponse;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.UhuSphereType;
import com.bezirk.sphere.impl.UhuSphere;
import com.bezirk.sphere.security.CryptoEngine;

/**
 * @author ajc6kor
 *
 */
public class SphereDiscoveryTest {
	
	
	private static final String sphereId = "TestSphere";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceB");
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceId );
	private static final UhuServiceEndPoint serviceBEndPoint = new UhuServiceEndPoint(new UhuServiceId("ServiceB"));

	private static final String requestKey = "REQUEST_KEY";
	private static InetAddress inetAddr;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		inetAddr = getInetAddress();
		recipient.device = inetAddr.getHostAddress();
		serviceBEndPoint.device = inetAddr.getHostAddress();
	}

	@Test
	public void test() {

		int discoveryId=12;
		DiscoveryLabel dlabel = new DiscoveryLabel(recipient, discoveryId);
		long timeout=10000;
		int max=1;
		SphereDiscoveryRecord disc = new SphereDiscoveryRecord(sphereId,timeout, max);
		
		UPADeviceInterface upaDevice = null;
		SphereRegistry sphereRegistry = new SphereRegistry();
		CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);
		UhuSphere uhuSphere = new UhuSphere(cryptoEngine, upaDevice, sphereRegistry);
		
		SphereDiscovery sphereDiscovery = new SphereDiscovery(uhuSphere);
		
		sphereDiscovery.addRequest(dlabel, disc);
		
		DiscoveryLabel dlabelTemp = new DiscoveryLabel(serviceBEndPoint, 14);
		sphereDiscovery.addRequest(dlabelTemp, disc);

		assertEquals("DiscoveredMap size is not equal to the number of requests added",2,getDiscoveredMapsize(sphereDiscovery));
		
		
		SphereDiscoveryResponse response = new SphereDiscoveryResponse(recipient, sphereId, requestKey, discoveryId);
		UhuSphereInfo uhuSphereInfo = new UhuSphereInfo(sphereId, "Test", UhuSphereType.UHU_SPHERE_TYPE_HOME, null, null);
		response.setUhuSphereInfo(uhuSphereInfo );
		
		assertTrue("Unable to add response to SphereDiscovery.",sphereDiscovery.addResponse(response ));
		
		sphereDiscovery.remove(dlabelTemp);
		assertEquals("DiscoveredMap size is not equal to 1 after removing entry.",1,getDiscoveredMapsize(sphereDiscovery));
		
		UhuServiceEndPoint invalidRecepient = new UhuServiceEndPoint(null);
		invalidRecepient.device = getInetAddress().getHostAddress();
		response = new SphereDiscoveryResponse(invalidRecepient , sphereId, requestKey, discoveryId);
		response.setUhuSphereInfo(uhuSphereInfo);
		assertFalse("Sphere Discovery response is added even when recepient is having null serviceID",sphereDiscovery.addResponse(response));
		
		
		response = new SphereDiscoveryResponse(recipient , sphereId, requestKey, 24);
		response.setUhuSphereInfo(uhuSphereInfo);
		assertFalse("Sphere Discovery response is added even when discovery id is invalid.",sphereDiscovery.addResponse(response));
		
	}
	
	private int getDiscoveredMapsize(SphereDiscovery sphereDiscovery) {
		
		int discoveredMapSize =0;
		
		try {
			if(sphereDiscovery.getDiscoveredMap()!=null)
				
				discoveredMapSize = sphereDiscovery.getDiscoveredMap().size();
			
		} catch (InterruptedException e) {

			fail("Unable to retrieve discovered map.");
		
		}
		return discoveredMapSize;
	}
	
	private static InetAddress getInetAddress() {
		try {

			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {

						inetAddr = UhuNetworkUtilities.getIpForInterface(intf);
						return inetAddr;
					}

				}
			}
		} catch (SocketException e) {

			fail("Unable to fetch network interface");

		}
		return null;
	}


}
