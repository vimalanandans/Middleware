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
package com.bezirk.middleware.messages;

import com.bezirk.middleware.identity.Alias;

/**
 * <h1 style="color: red">Experimental</h1>
 *
 * For identified events, the sending middleware attaches its alias to them before sending them on
 * the wire. This allows recipients to determine who the message is about. Zirks do not need to do
 * anything special to send an identified message. The middleware recognizes outgoing identified
 * messages and attaches the current user's alias.
 *
 * Zirks determine who a message is about by calling {@link #getAlias()}, then using
 * {@link com.bezirk.middleware.identity.IdentityManager#isMiddlewareUser(Alias)} to determine
 * whether or not the message is about the current user of the middleware or someone else:
 * <pre>
 *     // ...
 *
 *     // Receiving Zirk
 *     carObservations.setEventListener((event) -&gt; {
 *         if (event instanceof HvacObservation) {
 *             // HvacObservation is an IdentifiedEvent
 *             HvacObservation o = (HvacObservation) event;
 *             if (o.isMiddlewareUser()) {
 *                 // Do something with the observation knowing it is
 *                 // about the user of this Zirk
 *             } else {
 *                 // Do something with the observation knowing it is
 *                 // about someone in the same subnet as this Zirk's
 *                 // user, but it is not about this Zirk's user.
 *             }
 *             // ...
 *         }
 *         // ...
 *     });
 * </pre>
 */
public class IdentifiedEvent extends Event {

    private static final long serialVersionUID = 1259872377550330321L;
    private Alias alias;

    private boolean isMiddlewareUser;

    /**
     * Check is this identified message is for the user of the middleware receiving the message.
     *
     * @return <code>true</code>  if the <code>alias</code> in this message belongs to the
     * receiving middleware's user
     */
    public boolean isMiddlewareUser() {
        return isMiddlewareUser;
    }

    /**
     * Set by the middleware to inform zirk is this identified incoming message belong to this middleware
     * Zirk setting
     *
     * @param middlewareUser middleware sets to <code>true</code> if the <code>alias</code> in
     *                       this message belongs to the receiving middleware's user
     */
    public void setMiddlewareUser(boolean middlewareUser) {
        isMiddlewareUser = middlewareUser;
    }


    /**
     * Return the alias for the middleware user this event is about.
     *
     * @return the alias for the middleware user this event is about.
     */
    public Alias getAlias() {
        return alias;
    }

    /**
     * The middleware calls this method when an identified event is sent to attach an identity. If
     * a Zirk calls this method before sending a message, the middleware will overwrite the
     * Zirk-specified identity. This prevents Zirks from spoofing identities.
     *
     * @param alias the identity to attach
     */
    public void setAlias(Alias alias) {
        this.alias = alias;
    }
}
