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
package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

/**
 * This control message is issued by owners to ask other services (mainly members) to leave
 */
public class BezirkSphereLeave extends UnicastControlMessage {
    private static final Discriminator discriminator = ControlMessage.Discriminator.BEZIRK_SPHERE_LEAVE;
    private final ZirkId serviceId;
    private final long time;

    /**
     * Used for sending leave request to a member
     */
    public BezirkSphereLeave(String sphereID, ZirkId serviceId, BezirkZirkEndPoint sender,
                             BezirkZirkEndPoint recipient) {
        super(sender, recipient, sphereID, discriminator, true);
        //public UnicastControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereName,
        //String discriminator, Boolean retransmit, String key){
        this.serviceId = serviceId;
        this.time = System.currentTimeMillis();
    }

    /**
     * @param json The Json String that is to be deserialized
     * @param dC   class to fromJson into
     * @return object of class type C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return gson.fromJson(json, dC);
    }

    public ZirkId getServiceId() {
        return serviceId;
    }

    public long getTime() {
        return time;
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}
