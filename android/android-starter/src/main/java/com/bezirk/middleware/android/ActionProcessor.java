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
    public void processBezirkAction(Intent intent, AndroidProxyServer ProxyService,
                                    LifeCycleCallbacks lifeCycleCallbacks) {


        BezirkAction intentAction = BezirkAction.getActionFromString(intent.getAction());

        if (ValidatorUtility.isObjectNotNull(intentAction)) {
            logger.debug("intentAction is not null in Actionprocessor");
            if (logger.isDebugEnabled())
                logger.debug("Received intent, action: {}", intentAction.getName());

            BezirkAction.ActionType actionType = intentAction.getType();
            logger.debug("actionType is "+actionType);
            switch (actionType) {
                case BEZIRK_STACK_ACTION:
                    processBezirkStackAction(intent, intentAction, lifeCycleCallbacks);
                    break;
                case DEVICE_ACTION:
                    logger.warn("Not handling device actions in Bezirk currently.");
                    //processDeviceActions(intentAction, service);
                    break;
                case SEND_ACTION:
                    logger.debug("processBezirkAction method");
                    processSendActions(intentAction, intent, ProxyService);
                    break;
                case ZIRK_ACTION:
                    processZirkActions(intentAction, intent, ProxyService);
                    break;
                default:
                    logger.warn("Received unknown intent action: " + intentAction.getName());
                    break;
            }
        } else {
            if (logger.isWarnEnabled())
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
                logger.debug("Not handling Reboot");
                break;
            case ACTION_CLEAR_PERSISTENCE:
                logger.debug("Not handling clear persistance");
                break;
            default:
                if (logger.isWarnEnabled())
                    logger.warn("Received unknown intent action: {}", intent.getAction());
                break;
        }
    }

    private void processZirkActions(BezirkAction intentAction, Intent intent,
                                    AndroidProxyServer ProxyService) {
        switch (intentAction) {
            case ACTION_BEZIRK_REGISTER:
                ProxyService.registerZirk(intent);
                break;
            case ACTION_BEZIRK_SUBSCRIBE:
                ProxyService.subscribeService(intent);
                break;
            case ACTION_BEZIRK_SET_LOCATION:
                ProxyService.setLocation(intent);
                break;
            case ACTION_BEZIRK_UNSUBSCRIBE:
                ProxyService.unsubscribeService(intent);
                break;
            default:
                if (logger.isWarnEnabled())
                    logger.warn("Received unknown intent action: {}", intent.getAction());
                break;
        }
    }

    private void processSendActions(BezirkAction intentAction, Intent intent,
                                    AndroidProxyServer ProxyService) {
        logger.debug("intentAction in ActionProcessor is "+intentAction);
        switch (intentAction) {
            case ACTION_ZIRK_SEND_MULTICAST_EVENT:
                logger.debug("In ACTION_ZIRK_SEND_MULTICAST_EVENT");
                ProxyService.sendMulticastEvent(intent);
                break;
            case ACTION_ZIRK_SEND_UNICAST_EVENT:
                ProxyService.sendUnicastEvent(intent);
                break;
            case ACTION_BEZIRK_PUSH_UNICAST_STREAM:
                ProxyService.sendUnicastStream(intent);
                break;
            default:
                if (logger.isWarnEnabled())
                    logger.warn("Received unknown intent action: {}", intent.getAction());
                break;
        }
    }
}