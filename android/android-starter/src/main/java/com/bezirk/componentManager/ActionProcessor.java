package com.bezirk.componentManager;

import android.content.Intent;

import com.bezirk.actions.BezirkAction;
import com.bezirk.proxy.android.AndroidProxyServer;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of processing intent action based on type.
 * Handles zirk actions, send actions, stack actions and bezirk device actions.
 */
public final class ActionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ActionProcessor.class);

    private static final String CONTROL_UI_NOTIFICATION_ACTION = "com.bezirk.controlui.DeviceControlActivity";
    private static final int START_CODE = 100;
    private static final int STOP_CODE = 101;

    /**
     * Process BezirkAction based on action type.
     */

    public void processBezirkAction(Intent intent, AndroidProxyServer ProxyService, ComponentManager.LifeCycleCallbacks lifeCycleCallbacks) {


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


    private void processBezirkStackAction(Intent intent, BezirkAction intentAction, ComponentManager.LifeCycleCallbacks lifeCycleCallbacks) {

        switch (intentAction) {
            case ACTION_START_BEZIRK:
                lifeCycleCallbacks.start();
                break;
            case ACTION_STOP_BEZIRK:
                lifeCycleCallbacks.stop();
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

//    private void processDeviceActions(BezirkAction intentAction, MainService service) {
//
//        switch (intentAction) {
//
//            case ACTION_CHANGE_DEVICE_NAME:
//                sendIntent(ActionCommands.CMD_CHANGE_DEVICE_NAME_STATUS, true, service);
//                break;
//            case ACTION_CHANGE_DEVICE_TYPE:
//                sendIntent(ActionCommands.CMD_CHANGE_DEVICE_TYPE_STATUS, true, service);
//                break;
//            case ACTION_DEV_MODE_ON:
//                sendIntent(ActionCommands.CMD_DEV_MODE_ON_STATUS, MainStackHandler.getDevMode().switchMode(DevMode.Mode.ON), service);
//                break;
//            case ACTION_DEV_MODE_OFF:
//                sendIntent(ActionCommands.CMD_DEV_MODE_OFF_STATUS, MainStackHandler.getDevMode().switchMode(DevMode.Mode.OFF), service);
//                break;
//            case ACTION_DEV_MODE_STATUS:
//                DevMode.Mode mode = getDevMode();
//                sendIntent(ActionCommands.CMD_DEV_MODE_STATUS, mode, service);
//                break;
//            default:
//                if (logger.isWarnEnabled())
//                    logger.warn("Received unknown intent action: {}", intentAction.getName());
//                break;
//        }
//
//    }

//    private DevMode.Mode getDevMode() {
//        return MainStackHandler.getDevMode() == null ? DevMode.Mode.OFF :
//                MainStackHandler.getDevMode().getStatus();
//    }

    private void processZirkActions(BezirkAction intentAction, Intent intent, AndroidProxyServer ProxyService) {
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

    private void processSendActions(BezirkAction intentAction, Intent intent, AndroidProxyServer ProxyService) {
        logger.debug("intentAction in ActionProcessor is "+intentAction);
        switch (intentAction) {
            case ACTION_ZIRK_SEND_MULTICAST_EVENT:
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

//    private void sendIntent(String command, boolean status, MainService service) {
//        Intent intent = new Intent();
//        intent.setAction(CONTROL_UI_NOTIFICATION_ACTION);
//        intent.putExtra("Command", command);
//        intent.putExtra("Status", status);
//
//        service.getApplicationContext().sendBroadcast(intent);
//    }
//
//    private void sendIntent(String command, DevMode.Mode mode, MainService service) {
//        Intent intent = new Intent();
//        intent.setAction(CONTROL_UI_NOTIFICATION_ACTION);
//        intent.putExtra("Command", command);
//        intent.putExtra("Mode", mode);
//        service.getApplicationContext().sendBroadcast(intent);
//    }
}