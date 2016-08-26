package com.bezirk.middleware.core.control.messages;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

public class UnicastControlMessage extends ControlMessage {

   // private Boolean isLocal = false;
    private BezirkZirkEndPoint recipient;

    /**
     * Empty Constructor required for gson.fromJson
     */
    public UnicastControlMessage() {
        // Empty Constructor required for gson.fromJson
    }

    /**
     * Used if you want to send a custom key
     * Generally only used with responses
     *
     * @param sender        the sender-end-point
     * @param recipient     the recipient-end-point
     * @param sphereId      the sphere-id
     * @param discriminator the message discriminator Eg: DiscoveryRequest, StreamResponse
     * @param retransmit    <code>true</code> if the message is to be re-transmitted
     * @param key           UniqueKey that is used to match responses to corresponding requests
     */
    public UnicastControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereId,
                                 Discriminator discriminator, Boolean retransmit, String key) {
        super(sender, sphereId, discriminator, retransmit, key);
        this.recipient = recipient;
    }

    /**
     * Used if you want the stack to auto-generate the key
     * Generally only used with requests
     *
     * @param sender        the sender-end-point
     * @param recipient     the recipient-end-point
     * @param sphereId      the sphere-id
     * @param discriminator the message discriminator Eg: DiscoveryRequest, StreamResponse
     * @param retransmit    <code>true</code> if the message is to be re-transmitted
     */
    public UnicastControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereId,
                                 Discriminator discriminator, Boolean retransmit) {
        super(sender, sphereId, discriminator, retransmit);
        this.recipient = recipient;
    }

    public BezirkZirkEndPoint getRecipient() {
        return recipient;
    }

}
