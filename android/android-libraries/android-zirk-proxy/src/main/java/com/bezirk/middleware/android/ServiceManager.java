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
package com.bezirk.middleware.android;

import com.bezirk.middleware.core.actions.StartServiceAction;
import com.bezirk.middleware.core.actions.StopServiceAction;
import com.bezirk.middleware.core.proxy.Config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
    private boolean started = false;
    private final IntentSender intentSender;

    public ServiceManager(@NotNull final IntentSender intentSender) {
        this.intentSender = intentSender;
    }

    void start(@NotNull final Config config) {
        if (intentSender.sendBezirkIntent(new StartServiceAction(config))) {
            logger.info("Bezirk is starting");
            started = true;
        } else {
            logger.error("Unable to start Bezirk Service");
        }
    }

    void stop() {
        if (BezirkMiddleware.isLocalBezirkService()) {
            if (started) {
                // If stop action was successfully sent, started is set to false.
                started = !intentSender.sendBezirkIntent(new StopServiceAction());
            } else {
                logger.warn("Bezirk Service is not running.");
            }
        } else {
            logger.debug("Not stopping bezirk. Bezirk not running within the same app.");
        }
    }

    boolean isStarted() {
        return started;
    }


}
