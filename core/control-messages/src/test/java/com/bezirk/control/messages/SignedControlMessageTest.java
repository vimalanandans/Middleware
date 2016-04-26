/**
 *
 */
package com.bezirk.control.messages;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezrik.network.UhuNetworkUtilities;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the SignedControlMessage POJO by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class SignedControlMessageTest {

    private static final Logger log = LoggerFactory
            .getLogger(SignedControlMessageTest.class);

    private static final Discriminator discriminator = Discriminator.DiscoveryRequest;
    private static final String sphereId = "TestSphere";
    private static final BezirkZirkId serviceAId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceAId);
    private static final BezirkZirkId serviceBId = new BezirkZirkId("ServiceB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(serviceBId);
    private static final String key = "TestKey";

    private static InetAddress inetAddr;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up SignedControlMessageTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down SignedControlMessageTest TestCase *****");
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

                        inetAddr = UhuNetworkUtilities.getIpForInterface(intf);
                        return inetAddr;
                    }

                }
            }
        } catch (SocketException e) {

            log.error("Unable to fetch network interface");

        }
        return null;
    }

    @Test
    public void testSignedControlMessage() {

        com.bezirk.control.messages.SignedControlMessage ctrlMessage = new com.bezirk.control.messages.SignedControlMessage(sender, recipient, null, sphereId, discriminator);
        String serializedMessage = ctrlMessage.serialize();
        com.bezirk.control.messages.SignedControlMessage deserializedCtrlMessage = com.bezirk.control.messages.SignedControlMessage.deserialize(serializedMessage, com.bezirk.control.messages.SignedControlMessage.class);
        assertEquals("Deserialized control message is not equalt to the original object. Discriminator is differing.", ctrlMessage.getDiscriminator(), deserializedCtrlMessage.getDiscriminator());
        assertEquals("Deserialized control message is not equalt to the original object. SphereId is differing.", ctrlMessage.getSphereId(), deserializedCtrlMessage.getSphereId());
    /*--- TO BE UNCOMMENTED ONCE THE UHUSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in UhuServiceEndpoint equals api.
	 *
	assertEquals("Deserialized control message is not equalt to the original object. Recipient is differing.",ctrlMessage.getRecipient(), deserializedCtrlMessage.getRecipient());
	assertEquals("Deserialized control message is not equalt to the original object. Sender is differing.",ctrlMessage.getSender(), deserializedCtrlMessage.getSender());
	*/

        ctrlMessage = new com.bezirk.control.messages.SignedControlMessage(sender, recipient, null, sphereId, discriminator, key);
        serializedMessage = ctrlMessage.serialize();
        deserializedCtrlMessage = com.bezirk.control.messages.SignedControlMessage.deserialize(serializedMessage, com.bezirk.control.messages.SignedControlMessage.class);
        assertEquals("Deserialized control message is not equalt to the original object. Discriminator is differing.", ctrlMessage.getDiscriminator(), deserializedCtrlMessage.getDiscriminator());
        assertEquals("Deserialized control message is not equalt to the original object. SphereId is differing.", ctrlMessage.getSphereId(), deserializedCtrlMessage.getSphereId());
        assertEquals("Deserialized control message is not equalt to the original object. Key is differing.", ctrlMessage.getUniqueKey(), deserializedCtrlMessage.getUniqueKey());
	/*--- TO BE UNCOMMENTED ONCE THE UHUSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in UhuServiceEndpoint equals api.
	 *
	assertEquals("Deserialized control message is not equalt to the original object. Recipient is differing.",ctrlMessage.getRecipient(), deserializedCtrlMessage.getRecipient());
	assertEquals("Deserialized control message is not equalt to the original object. Sender is differing.",ctrlMessage.getSender(), deserializedCtrlMessage.getSender());
	*/
    }


}
