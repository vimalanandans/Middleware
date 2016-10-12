package com.bezirk.middleware.android.proxy.android;

import android.content.Intent;
import android.util.Log;

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.RegisterZirkAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.SetLocationAction;
import com.bezirk.middleware.core.actions.SubscriptionAction;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.middleware.core.proxy.ProxyServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AndroidProxyServer extends ProxyServer {
    private static final Logger logger = LoggerFactory.getLogger(AndroidProxyServer.class);

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

        super.sendEvent(eventAction);
    }

    public void sendUnicastEvent(Intent intent) {
        logger.trace("Received unicast message from zirk");

        final UnicastEventAction eventAction =
                (UnicastEventAction) intent.getSerializableExtra(BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT.getName());

        super.sendEvent(eventAction);
    }

    public void setLocation(Intent intent) {
        SetLocationAction locationAction = (SetLocationAction) intent.getSerializableExtra(BezirkAction.ACTION_BEZIRK_SET_LOCATION.getName());

        logger.trace("Received location " + locationAction.getLocation() + " from zirk");

        super.setLocation(locationAction);
    }
}
