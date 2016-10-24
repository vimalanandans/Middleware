/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.android.proxy.android;

import android.content.Intent;

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.EventAction;
import com.bezirk.middleware.core.actions.RegisterZirkAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.SetLocationAction;
import com.bezirk.middleware.core.actions.SubscriptionAction;
import com.bezirk.middleware.core.componentManager.LifeCycleObservable;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.middleware.core.proxy.ProxyServer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AndroidProxyServer extends ProxyServer implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(AndroidProxyServer.class);
    private ExecutorService senderExecutorService;

    public AndroidProxyServer(IdentityManager identityManager) {
        super(identityManager);
    }

    public void registerZirk(Intent intent) {
        final RegisterZirkAction registrationAction =
                (RegisterZirkAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_REGISTER.getName());

        logger.trace("Zirk registration received by Bezirk. Name: " + registrationAction.getZirkName()
                + ", ID: {}" + registrationAction.getZirkId());

        super.registerZirk(registrationAction);
    }

    public void subscribeService(Intent intent) {
        logger.trace("Received subscription from zirk");

        final SubscriptionAction subscriptionAction =
                (SubscriptionAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_SUBSCRIBE.getName());

        super.subscribe(subscriptionAction);
    }

    public void unsubscribeService(Intent intent) {
        logger.trace("Received unsubscribe from zirk");

        final SubscriptionAction subscriptionAction =
                (SubscriptionAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_UNSUBSCRIBE.getName());

        if (subscriptionAction.getMessageSet() != null) {
            super.unsubscribe(subscriptionAction);
        } else {
            super.unregister(new RegisterZirkAction(subscriptionAction.getZirkId(), null));
        }
    }

    public void sendMulticastEvent(Intent intent) {
        logger.trace("Received multicast message from zirk");

        final SendMulticastEventAction eventAction =
                (SendMulticastEventAction) intent.getSerializableExtra(BezirkAction.ACTION_ZIRK_SEND_MULTICAST_EVENT.getName());

        //sendEvent(eventAction);
        sendEventUsingExecutor(eventAction);

    }

    public void sendUnicastEvent(Intent intent) {
        logger.trace("Received unicast message from zirk");

        final UnicastEventAction eventAction =
                (UnicastEventAction) intent.getSerializableExtra(BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT.getName());

        //sendEvent(eventAction);
        sendEventUsingExecutor(eventAction);

    }

    public void setLocation(Intent intent) {
        SetLocationAction locationAction = (SetLocationAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_SET_LOCATION.getName());

        logger.trace("Received location " + locationAction.getLocation() + " from zirk");

        super.setLocation(locationAction);
    }

    @Override
    public void update(Observable observable, Object data) {
        LifeCycleObservable lifeCycleObservable = (LifeCycleObservable) observable;
        switch (lifeCycleObservable.getState()) {
            case RUNNING:
                createExecutor();
                break;
            case STOPPED:
                stopExecutor();
                break;
        }
    }

    private void sendEventUsingExecutor(@NotNull final EventAction eventAction) {
        if (senderExecutorService == null) {
            throw new IllegalStateException("senderExecutorService is not created");
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (eventAction instanceof SendMulticastEventAction) {
                    sendEvent((SendMulticastEventAction) eventAction);
                } else if (eventAction instanceof UnicastEventAction) {
                    sendEvent((UnicastEventAction) eventAction);
                } else {
                    logger.error("Event action not support");
                    return;
                }
            }
        };
        senderExecutorService.submit(runnable);
    }

    private void createExecutor() {
        if (senderExecutorService == null) {
            senderExecutorService = Executors.newSingleThreadExecutor();
            logger.debug("ExecutorService created");
        }
    }

    private void stopExecutor() {
        if (senderExecutorService != null) {
            // Disable new tasks from being submitted
            senderExecutorService.shutdown();
            try {
                // Wait a while for existing tasks to terminate
                if (!senderExecutorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    senderExecutorService.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!senderExecutorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                        System.err.println("Pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                senderExecutorService.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }
}
