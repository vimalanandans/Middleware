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
package com.bezirk.middleware.core.proxy;

import com.bezirk.middleware.core.actions.RegisterZirkAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.SetLocationAction;
import com.bezirk.middleware.core.actions.SubscriptionAction;
import com.bezirk.middleware.core.identity.IdentityProvisioner;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.middleware.core.pubsubbroker.PubSubBrokerZirkServicer;

import org.jetbrains.annotations.NotNull;

public class ProxyServer {
    private PubSubBrokerZirkServicer pubSubBrokerService;
    private final IdentityManager identityManager;

    public ProxyServer(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    public void registerZirk(@NotNull RegisterZirkAction registerZirkAction) {
        pubSubBrokerService.registerZirk(registerZirkAction.getZirkId(), registerZirkAction.getZirkName());
    }

    public void subscribe(@NotNull SubscriptionAction subscriptionAction) {
        pubSubBrokerService.subscribe(subscriptionAction.getZirkId(), subscriptionAction.getMessageSet());
    }

    public void sendEvent(@NotNull SendMulticastEventAction eventAction) {
        if (eventAction.isIdentified()) {
            eventAction.setAlias(((IdentityProvisioner) identityManager).getAlias());
        }

        pubSubBrokerService.sendMulticastEvent(eventAction);
    }

    public void sendEvent(@NotNull UnicastEventAction eventAction) {
        if (eventAction.isIdentified()) {
            eventAction.setAlias(((IdentityProvisioner) identityManager).getAlias());
        }

        pubSubBrokerService.sendUnicastEvent(eventAction);
    }

    public void setLocation(@NotNull SetLocationAction locationAction) {
        pubSubBrokerService.setLocation(locationAction.getZirkId(), locationAction.getLocation());
    }

    public boolean unsubscribe(@NotNull SubscriptionAction subscriptionAction) {
        return pubSubBrokerService.unsubscribe(subscriptionAction.getZirkId(), subscriptionAction.getMessageSet());
    }

    public boolean unregister(@NotNull RegisterZirkAction registerZirkAction) {
        return pubSubBrokerService.unregisterZirk(registerZirkAction.getZirkId());
    }

    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setPubSubBrokerService(PubSubBrokerZirkServicer pubSubBrokerService) {
        this.pubSubBrokerService = pubSubBrokerService;
    }
}
