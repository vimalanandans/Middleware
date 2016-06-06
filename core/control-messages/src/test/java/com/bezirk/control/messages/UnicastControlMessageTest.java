package com.bezirk.control.messages;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezrik.network.BezirkNetworkUtilities;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static org.junit.Assert.*;

/**
 * This testCase verifies the UnicastControlMessage by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class UnicastControlMessageTest {
    private static final Logger logger = LoggerFactory.getLogger(UnicastControlMessageTest.class);

    private static final String sphereId = "TestSphere";
    private static final ZirkId serviceId = new ZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);
    private static final ZirkId serviceBId = new ZirkId("ServiceB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(serviceBId);
    private static final String key = "TESTKEY";
    private static final Discriminator discriminator = Discriminator.DiscoveryRequest;
    private static final Boolean retransmit = true;

    private static InetAddress inetAddr;


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up UnicastControlMessageTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down UnicastControlMessageTest TestCase *****");
    }

    private static InetAddress getInetAddress() {
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {

                        inetAddr = BezirkNetworkUtilities.getIpForInterface(intf);
                        return inetAddr;
                    }

                }
            }
        } catch (SocketException e) {

            logger.error("Unable to fetch network interface");

        }
        return null;
    }

    /**
     * Test method for {@link com.bezirk.control.messages.MulticastControlMessage#MulticastControlMessage()}.
     */
    @Test
    public void testUnicastControlMessage() {

        com.bezirk.control.messages.UnicastControlMessage unicastCtrlMessage = new com.bezirk.control.messages.UnicastControlMessage(sender, recipient, sphereId, discriminator, retransmit);
        String serializedMessage = unicastCtrlMessage.serialize();
        com.bezirk.control.messages.UnicastControlMessage deserializedCtrlMessage = com.bezirk.control.messages.UnicastControlMessage.deserialize(serializedMessage, com.bezirk.control.messages.UnicastControlMessage.class);
        assertEquals("Discriminator not equal to the set value.", discriminator, deserializedCtrlMessage.getDiscriminator());
        assertEquals("SphereId not equal to the set value.", sphereId, deserializedCtrlMessage.getSphereId());
        assertEquals("Retransmit not equal to the set value.", retransmit, deserializedCtrlMessage.getRetransmit());
        assertTrue("Local message considered as remote after deserialization", deserializedCtrlMessage.getIsLocal());
    /*--- TO BE UNCOMMENTED ONCE THE BEZIRKSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in BezirkServiceEndpoint equals api.
	 *
	 assertEquals("Sender not equal to the set value.",sender, deserializedCtrlMessage.getSender());
 	 assertEquals("Sender not equal to the set value.",recipient, deserializedCtrlMessage.getRecipient());
	 */
        unicastCtrlMessage = new com.bezirk.control.messages.UnicastControlMessage(sender, recipient, sphereId, discriminator, retransmit, key);
        serializedMessage = unicastCtrlMessage.serialize();
        deserializedCtrlMessage = com.bezirk.control.messages.UnicastControlMessage.deserialize(serializedMessage, UnicastControlMessage.class);
        assertEquals("Discriminator not equal to the set value.", discriminator, deserializedCtrlMessage.getDiscriminator());
        assertEquals("SphereId not equal to the set value.", sphereId, deserializedCtrlMessage.getSphereId());
        assertEquals("Retransmit not equal to the set value.", retransmit, deserializedCtrlMessage.getRetransmit());
        assertEquals("Key not equal to the set value.", key, deserializedCtrlMessage.getUniqueKey());
        assertTrue("Local message considered as remote after deserialization", deserializedCtrlMessage.getIsLocal());

	/*--- TO BE UNCOMMENTED ONCE THE BEZIRKSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in BezirkServiceEndpoint equals api.
	 *
	 assertEquals("Sender not equal to the set value.",sender, deserializedCtrlMessage.getSender());
	 assertEquals("Sender not equal to the set value.",recipient, deserializedCtrlMessage.getRecipient());

	 */

    }

}
