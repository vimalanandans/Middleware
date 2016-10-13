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
package com.bezirk.middleware.core.identity;

import com.bezirk.middleware.identity.Alias;

/**
 * Internal API for creating and managing identities. This interface must not be exposed to
 * Zirks, otherwise they can spoof the identities of other Bezirk users whose identities they have
 * seen.
 */
public interface IdentityProvisioner {


    /**
     * Create a new identity with the human-readable identifier <code>name</code>.
     *
     * @param name the human-readable identifier for this alias
     * @return the new identity or <code>null</code> if required cryptography algorithms do not exist
     */
    Alias createIdentity(String name);

    /**
     * Set the identity the middleware instance is using to <code>identity</code>. After this
     * method is called, any {@link com.bezirk.middleware.messages.IdentifiedEvent identified event}
     * sent by a zirk will have this identity attached.
     *
     * @param identity the identity this middleware instance should use
     */
    void setIdentity(Alias identity);

    /**
     * Get the current alias set for the middleware so that it can be added to a message
     * by the Zirk proxy server.
     */
    Alias getAlias();
}
