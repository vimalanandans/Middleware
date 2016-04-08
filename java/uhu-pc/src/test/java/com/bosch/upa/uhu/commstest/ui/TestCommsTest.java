package com.bosch.upa.uhu.commstest.ui;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.bosch.upa.devices.UPADeviceForPC;
import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.UhuComms;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;

public class TestCommsTest {
	int pingCount = 5;

	CommsTest commsTest;
	UPADeviceInterface upaDeviceInterface;
	UPADeviceForPC upaDevice = Mockito.mock(UPADeviceForPC.class);
	private boolean recievedPing = false;

	@Before
	public void init() {
		upaDevice.setDeviceLocation(null);
		upaDevice.setDeviceName("Test-PC");
		UhuCompManager.setUpaDevice(upaDevice);
		getInetAddress();
		IUpdateResponseMock responseUT = new IUpdateResponseMock();
		commsTest = new CommsTest(responseUT);
	}

	@Test
	public void testSendPing() {
		commsTest.sendPing(pingCount);
		assertTrue("Ping not received.",recievedPing);
	}

	@Test
	public void testGetSelectedServices() {
		assertNull("Pong is not null when testPingID is not null",
				commsTest.getSelectedServices("testPingId"));

	}

	private void getInetAddress() {
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
						UhuNetworkUtilities.getIpForInterface(intf);
						UhuComms.setINTERFACE_NAME(intf.toString());

					}

				}
			}
		} catch (SocketException e) {
			fail("Unable to fetch network interface");
		}

	}

	class IUpdateResponseMock implements IUpdateResponse {

		@Override
		public void updatePingResposne(String response) {
		    //Nothing to be done

		}

		@Override
		public void updateUIPingSent(PingMessage msg) {
			if (pingCount == msg.pingId) {

				recievedPing = true;
			}

		}

		@Override
		public void updateUIPingReceived(PingMessage msg) {
		  //Nothing to be done
		    
		}

		@Override
		public void updateUIPongSent(PingMessage msg) {
		  //Nothing to be done
		}

		@Override
		public void updateUIPongReceived(PongMessage msg, int size) {
		  //Nothing to be done
		}

	}

}
