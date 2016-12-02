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
package com.bezirk.middleware.core.remotelogging;


import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.Ledger;

/**
 * Interface for the remote logging feature to the middleware
 */
public interface RemoteLog {

    /**
     * Constant label for ALL_SPHERES logging.
     */
    String ALL_SPHERES = "ALL-SPHERES";

    /**
     * set logger to enable or disable the remote logging
     * This starts the logging server and trigger other nodes client to listen to
     * enable the control logging for middleware message analysing purpose.
     * enableRemoteLogging must be true
     **/
    boolean enableLogging(boolean enableRemoteLogging, boolean enableControl,
                          boolean enableFileLogging, String[] sphereName);

    /**
     * check whether the remoteLogging is enabled or not
     */
    boolean isRemoteLoggingEnabled();


    /**
     * to send the incoming event message for logging
     */
    boolean sendRemoteLogToServer(Ledger ledger);

    /**
     * to send the incoming control message for logging
     */
    boolean sendRemoteLogToServer(ControlMessage message);

    //boolean isRemoteMessageValid(RemoteLoggingMessage logMessage);
}
