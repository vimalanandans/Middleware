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
package com.bezirk.middleware.identity;

/**
 * <h1 style="color: red">Experimental</h1>
 *
 * The identity manager for Bezirk-specific identities. This interface exposes methods useful to
 * Zirks that are making use of {@link com.bezirk.middleware.messages.IdentifiedEvent identified messages}.
 */
public interface IdentityManager {
    /**
     * Determines whether or not <code>alias</code> belongs to the current user of the middleware.
     * This is used when an identified event is received to determine whether or not the attached
     * alias belongs to the current user. This is useful in determining how to handle the data
     * in identified messages. For example, if the message is an observation about an action the
     * user performed and the alias belongs to the current user of the middleware it is a primary
     * observation. Otherwise, the observation is potentially not of interest to the receiving
     * Zirk or it may be used as social context.
     *
     * @param alias an alias attached to a received identified message
     * @return <code>true</code> if the alias belongs to the current user of the middleware
     */
    boolean isMiddlewareUser(Alias alias);
}
