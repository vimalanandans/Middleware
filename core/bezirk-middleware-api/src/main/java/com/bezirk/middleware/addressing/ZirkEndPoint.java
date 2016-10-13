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
package com.bezirk.middleware.addressing;

/**
 * An end point represents a Zirk that another Zirk can directly send messages to (e.g. unicast).
 * There are a couple ways to get a Zirk's <code>ZirkEndPoint</code> to directly send it messages:
 * <ul>
 * <li>Wait to receive a broadcasted message from the Zirk, in which case the <code>ZirkEndPoint</code>
 * will be received by the appropriate listener (e.g.
 * {@link com.bezirk.middleware.messages.EventSet.EventReceiver}).</li>
 * <li>Extend an <code>EventSet</code> and include a discovery message and a reply message.
 * Anyone subscribed to the set that receives the discovery message should use the reply to
 * notify the discovery sender of their existence and subscription to the set.</li>
 * </ul>
 * <p>
 * The Bezirk middleware implements this interface.
 * </p>
 */
public interface ZirkEndPoint {
// For now, this is a marker interface because there is nothing to offer to a Zirk API wise.
}
