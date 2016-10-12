package com.bezirk.middleware.core.control.messages;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

public class MulticastControlMessage extends ControlMessage {

    /**
     * Empty Constructor required for gson.fromJson
     */
    public MulticastControlMessage() {
        // Empty Constructor required for gson.fromJson
    }

    /**
     * Used if you want to send a custom key
     * Generally only used with responses
     * This constructor may not be used currently: leaving it here for later
     *
     * @param sender        the sender-end-point
     * @param sphereId      This is the sphereId
     * @param discriminator the message discriminator Eg: DISCOVERY_REQUEST, STREAM_RESPONSE
     * @param key           UniqueKey that is used to match responses to corresponding requests
     */
    public MulticastControlMessage(BezirkZirkEndPoint sender, String sphereId,
                                   Discriminator discriminator, String key) {
        //Notice last boolean is set to true : This is because all multicasts are retransmitted
        super(sender, sphereId, discriminator, true, key);
    }

    /**
     * Used if you want the stack to auto-generate the key
     * Generally only used with requests
     *
     * @param sender        the sender-end-point
     * @param sphereId      This is the sphereId
     * @param discriminator the message discriminator Eg: DISCOVERY_REQUEST, STREAM_RESPONSE
     */
    public MulticastControlMessage(BezirkZirkEndPoint sender, String sphereId,
                                   Discriminator discriminator) {
        //Notice last boolean is set to true : This is because all multicasts are retransmitted
        super(sender, sphereId, discriminator, true);
    }

}
