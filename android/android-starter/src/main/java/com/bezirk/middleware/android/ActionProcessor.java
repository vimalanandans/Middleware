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

import android.content.Intent;

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.StartServiceAction;
import com.bezirk.middleware.core.actions.StopServiceAction;
import com.bezirk.middleware.core.componentManager.LifeCycleCallbacks;
import com.bezirk.middleware.android.proxy.android.AndroidProxyServer;
import com.bezirk.middleware.core.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of processing intent action based on type.
 * Handles zirk actions, send actions, stack actions and bezirk device actions.
 */
public final class ActionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ActionProcessor.class);

    /**
     * Process BezirkAction based on action type.
     */
    public void processBezirkAction(Intent intent, AndroidProxyServer proxyService,
                                    LifeCycleCallbacks lifeCycleCallbacks) {

        BezirkAction intentAction = BezirkAction.getActionFromString(intent.getAction());

        if (ValidatorUtility.isObjectNotNull(intentAction)) {
            logger.trace("intentAction is not null in ActionProcessor");
            logger.debug("Received intent, action: {}", intentAction.getName());

            BezirkAction.ActionType actionType = intentAction.getType();
            logger.trace("actionType is " + actionType);
            switch (actionType) {
                case BEZIRK_STACK_ACTION:
                    processBezirkStackAction(intent, intentAction, lifeCycleCallbacks);
                    break;
                case DEVICE_ACTION:
                    logger.warn("Not handling device actions in Bezirk currently.");
                    //processDeviceActions(intentAction, service);
                    break;
                case SEND_ACTION:
                    processSendActions(intentAction, intent, proxyService);
                    break;
                case ZIRK_ACTION:
                    processZirkActions(intentAction, intent, proxyService);
                    break;
                default:
                    logger.warn("Received unknown intent action: " + intentAction.getName());
                    break;
            }
        } else {
            logger.warn("Received unknown intent action: {}", intent.getAction());
        }

    }

    private void processBezirkStackAction(Intent intent, BezirkAction intentAction,
                                          LifeCycleCallbacks lifeCycleCallbacks) {
        switch (intentAction) {
            case ACTION_START_BEZIRK:
                StartServiceAction startServiceAction =
                        (StartServiceAction) intent.getSerializableExtra(BezirkAction.ACTION_START_BEZIRK.getName());
                lifeCycleCallbacks.start(startServiceAction);
                break;
            case ACTION_STOP_BEZIRK:
                StopServiceAction stopServiceAction =
                        (StopServiceAction) intent.getSerializableExtra(BezirkAction.ACTION_START_BEZIRK.getName());
                lifeCycleCallbacks.stop(stopServiceAction);
                break;
            case ACTION_REBOOT:
                logger.trace("Not handling Reboot");
                break;
            case ACTION_CLEAR_PERSISTENCE:
                logger.trace("Not handling clear persistence");
                break;
            default:
                logger.warn("Received unknown intent action: {}", intent.getAction());
                break;
        }
    }

    private void processZirkActions(BezirkAction intentAction, Intent intent,
                                    AndroidProxyServer proxyService) {
        switch (intentAction) {
            case ACTION_BEZIRK_REGISTER:
                proxyService.registerZirk(intent);
                break;
            case ACTION_BEZIRK_SUBSCRIBE:
                proxyService.subscribeService(intent);
                break;
            case ACTION_BEZIRK_SET_LOCATION:
                proxyService.setLocation(intent);
                break;
            case ACTION_BEZIRK_UNSUBSCRIBE:
                proxyService.unsubscribeService(intent);
                break;
            default:
                logger.warn("Received unknown intent action: {}", intent.getAction());
                break;
        }
    }

    private void processSendActions(BezirkAction intentAction, Intent intent,
                                    AndroidProxyServer proxyService) {
        logger.trace("intentAction in ActionProcessor is " + intentAction);
        switch (intentAction) {
            case ACTION_ZIRK_SEND_MULTICAST_EVENT:
                logger.trace("In ACTION_ZIRK_SEND_MULTICAST_EVENT");
                proxyService.sendMulticastEvent(intent);
                break;
            case ACTION_ZIRK_SEND_UNICAST_EVENT:
                logger.trace("In ACTION_ZIRK_SEND_UNICAST_EVENT");
                proxyService.sendUnicastEvent(intent);
                break;
            case ACTION_BEZIRK_PUSH_UNICAST_STREAM:
                logger.trace("In ACTION_BEZIRK_PUSH_UNICAST_STREAM");
                proxyService.sendStream(intent);
                break;

            default:
                logger.warn("Received unknown intent action: {}", intent.getAction());
                break;
        }
    }
}
