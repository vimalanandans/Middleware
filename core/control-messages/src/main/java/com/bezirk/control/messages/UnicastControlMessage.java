package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezrik.network.UhuNetworkUtilities;

public class UnicastControlMessage extends ControlMessage {

    private Boolean isLocal = false;
    private UhuServiceEndPoint recipient;

    /**
     * Empty Constructor required for gson.fromJSON
     */
    public UnicastControlMessage() {
        // Empty Constructor required for gson.fromJSON
    }

    /**
     * Used if you want to send a custom key
     * Generally only used with responses
     *
     * @param sender        the sender-end-point
     * @param recipient     the recipient-end-point
     * @param sphereName    the sphere-id
     * @param discriminator the message discriminator Eg: DiscoveryRequest, StreamResponse
     * @param retransmit    {@value true} if the message is to be re-transmitted
     * @param key           UniqueKey that is used to match responses to corresponding requests
     */
    public UnicastControlMessage(UhuServiceEndPoint sender, UhuServiceEndPoint recipient, String sphereId,
                                 Discriminator discriminator, Boolean retransmit, String key) {
        super(sender, sphereId, discriminator, retransmit, key);
        this.recipient = recipient;
        //Check if msg is local
        // device is generic may (udp) or may not (zyre) contain the ip. check generic means
        if (recipient.device.equals(UhuNetworkUtilities.getLocalInet().getHostAddress())) {

            this.isLocal = true;
        }
    }

    /**
     * Used if you want the stack to auto-generate the key
     * Generally only used with requests
     *
     * @param sender        the sender-end-point
     * @param recipient     the recipient-end-point
     * @param sphereName    the sphere-id
     * @param discriminator the message discriminator Eg: DiscoveryRequest, StreamResponse
     * @param retransmit    {@value true} if the message is to be re-transmitted
     */
    public UnicastControlMessage(UhuServiceEndPoint sender, UhuServiceEndPoint recipient, String sphereId,
                                 Discriminator discriminator, Boolean retransmit) {
        super(sender, sphereId, discriminator, retransmit);
        this.recipient = recipient;
        //Check if msg is local
        if (recipient.device.equals(UhuNetworkUtilities.getLocalInet().getHostAddress())) {
            this.isLocal = true;
        }
    }

    public Boolean getIsLocal() {
        return isLocal;
    }

    public UhuServiceEndPoint getRecipient() {
        return recipient;
    }

}
