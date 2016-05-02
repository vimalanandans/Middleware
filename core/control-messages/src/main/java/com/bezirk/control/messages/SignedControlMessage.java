package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

import java.security.SignedObject;


public class SignedControlMessage extends UnicastControlMessage {
    private final SignedObject signedObject;

    /**
     * Used for requests since the key is generated by the stack
     *
     * @param sender
     * @param recipient
     * @param signedObject
     * @param sphereID
     * @param discriminator
     */
    public SignedControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, SignedObject signedObject, String sphereID,
                                Discriminator discriminator) {

        //retransmit is set to true since this SignedControlMessage is used for requests
        super(sender, recipient, sphereID, discriminator, true);
        this.signedObject = signedObject;

    }

    /**
     * Used for responses since the custom key (request key) would be used
     *
     * @param sender
     * @param recipient
     * @param signedObject
     * @param sphereID
     * @param discriminator
     * @param key
     */
    public SignedControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, SignedObject signedObject, String sphereID,
                                Discriminator discriminator, String key) {
        //super(sphereID, discriminator);

        //retransmit is set to false since this SignedControlMessage is used for responses
        super(sender, recipient, sphereID, discriminator, false, key);
        this.signedObject = signedObject;

    }

    public SignedObject getSignedObject() {
        return signedObject;
    }

}
