/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.control.messages;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

import java.security.SignedObject;


public class SignedControlMessage extends UnicastControlMessage {
    private final SignedObject signedObject;

    /**
     * Used for requests since the key is generated by the stack
     */
    public SignedControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, SignedObject signedObject, String sphereID,
                                Discriminator discriminator) {

        //retransmit is set to true since this SignedControlMessage is used for requests
        super(sender, recipient, sphereID, discriminator, true);
        this.signedObject = signedObject;

    }

    /**
     * Used for responses since the custom key (request key) would be used
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
