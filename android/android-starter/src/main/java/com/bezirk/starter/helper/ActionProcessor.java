package com.bezirk.starter.helper;

import android.content.Intent;


import com.bezirk.proxy.android.ProxyServerIntend;
import com.bezirk.sphere.api.DevMode;
import com.bezirk.starter.MainService;
import com.bezirk.starter.ActionCommands;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of processing intent action based on type.
 * Handles zirk actions, send actions, stack actions and bezirk device actions.
 */
public final class ActionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ActionProcessor.class);

    private static final String CONTROL_UI_NOTIFICATION_ACTION = "com.bosch.upa.uhu.controluinotfication";
    private static final int START_CODE = 100;
    private static final int STOP_CODE = 101;

    /**
     * Process BezirkAction based on action type.
     *
     * @param intent
     * @param service
     * @param ProxyService
     * @param mainStackHandler
     */
    public void processBezirkAction(Intent intent, MainService service, ProxyServerIntend ProxyService, MainStackHandler mainStackHandler) {

        INTENT_ACTIONS intentAction = INTENT_ACTIONS.getActionUsingMessage(intent.getAction());

        if (ValidatorUtility.isObjectNotNull(intentAction)) {
            logger.info("Intent Action > {}", intentAction.message);

            INTENT_ACTIONS.ACTION_TYPE actionType = intentAction.type;

            switch (actionType) {

                case BEZIRK_STACK_ACTION:
                    processBezirkStackAction(service, intent, intentAction, mainStackHandler);
                    break;
                case DEVICE_ACTION:
                    processDeviceActions(intentAction, service);
                    break;
                case SEND_ACTION:
                    processSendActions(intentAction, intent, ProxyService);
                    break;
                case SERVICE_ACTION:
                    processServiceActions(intentAction, intent, service, ProxyService);
                    break;
                default:
                    logger.warn("Received unknown intent action: " + intentAction.message);
                    break;
            }
        } else {

            logger.warn("Received unknown intent action: " + intent.getAction());
        }

    }

    private void processBezirkStackAction(MainService service, Intent intent, INTENT_ACTIONS intentAction, MainStackHandler mainStackHandler) {
        switch (intentAction) {
            case ACTION_START_BEZIRK:
                mainStackHandler.startStack(service);
                break;
            case ACTION_STOP_BEZIRK:
                mainStackHandler.stopStack(service);
                break;
            case ACTION_REBOOT:
                mainStackHandler.reboot(service);
                break;
            case ACTION_CLEAR_PERSISTENCE:
                mainStackHandler.clearPersistence(service);
                break;
            case ACTION_DIAG_PING:
                mainStackHandler.diagPing(intent);
                break;
            case ACTION_START_REST_SERVER:
                mainStackHandler.startStopRestServer(START_CODE);
                break;
            case ACTION_STOP_REST_SERVER:
                mainStackHandler.startStopRestServer(STOP_CODE);
                break;
            default:
                logger.warn("Received unknown intent action: " + intentAction.message);
                break;
        }
    }

    private void processDeviceActions(INTENT_ACTIONS intentAction, MainService service) {

        switch (intentAction) {

            case ACTION_CHANGE_DEVICE_NAME:
                sendIntent(ActionCommands.CMD_CHANGE_DEVICE_NAME_STATUS, true, service);
                break;
            case ACTION_CHANGE_DEVICE_TYPE:
                sendIntent(ActionCommands.CMD_CHANGE_DEVICE_TYPE_STATUS, true, service);
                break;
            case ACTION_DEV_MODE_ON:
                sendIntent(ActionCommands.CMD_DEV_MODE_ON_STATUS, MainStackHandler.getDevMode().switchMode(DevMode.Mode.ON), service);
                break;
            case ACTION_DEV_MODE_OFF:
                sendIntent(ActionCommands.CMD_DEV_MODE_OFF_STATUS, MainStackHandler.getDevMode().switchMode(DevMode.Mode.OFF), service);
                break;
            case ACTION_DEV_MODE_STATUS:
                DevMode.Mode mode = getDevMode();
                sendIntent(ActionCommands.CMD_DEV_MODE_STATUS, mode, service);
                break;
            default:
                logger.warn("Received unknown intent action: {}", intentAction.message);
                break;
        }

    }

    private DevMode.Mode getDevMode() {
        DevMode.Mode mode;
        if (MainStackHandler.getDevMode() == null) {
            // if user wifi supplicant status is Disconnected and on Init he clicks on DeviceControl, devMode will be null.
            mode = DevMode.Mode.OFF;
        } else {
            mode = MainStackHandler.getDevMode().getStatus();
        }
        return mode;
    }

    private void processServiceActions(INTENT_ACTIONS intentAction, Intent intent, MainService service, ProxyServerIntend ProxyService) {

        switch (intentAction) {

            case ACTION_BEZIRK_REGISTER:
                ProxyService.registerService(intent);
                break;
            case ACTION_BEZIRK_SUBSCRIBE:
                ProxyService.subscribeService(intent);
                break;
            case ACTION_BEZIRK_SETLOCATION:
                ProxyService.setLocation(intent);
                break;
            case ACTION_SERVICE_DISCOVER:
                ProxyService.discoverService(intent);
                break;
            case ACTION_BEZIRK_UNSUBSCRIBE:
                ProxyService.unsubscribeService(intent);
                break;
            case ACTION_PIPE_REQUEST:
                service.processPipeRequest(intent);
                break;
            default:
                logger.warn("Received unknown intent action: " + intentAction.message);
                break;
        }
    }

    private void processSendActions(INTENT_ACTIONS intentAction, Intent intent, ProxyServerIntend ProxyService) {

        switch (intentAction) {
            case ACTION_SERVICE_SEND_MULTICAST_EVENT:
                ProxyService.sendMulticastEvent(intent);
                break;
            case ACTION_SERVICE_SEND_UNICAST_EVENT:
                ProxyService.sendUnicastEvent(intent);
                break;
            case ACTION_BEZIRK_PUSH_UNICAST_STREAM:
                ProxyService.sendUnicastStream(intent);
                break;
            case ACTION_BEZIRK_PUSH_MULTICAST_STREAM:
                ProxyService.sendMulticastStream(intent);
                break;
            default:
                logger.warn("Received unknown intent action: " + intentAction.message);
                break;
        }
    }

    private void sendIntent(String command, boolean status, MainService service) {
        Intent intent = new Intent();
        intent.setAction(CONTROL_UI_NOTIFICATION_ACTION);
        intent.putExtra("Command", command);
        intent.putExtra("Status", status);

        service.getApplicationContext().sendBroadcast(intent);
    }

    private void sendIntent(String command, DevMode.Mode mode, MainService service) {
        Intent intent = new Intent();
        intent.setAction(CONTROL_UI_NOTIFICATION_ACTION);
        intent.putExtra("Command", command);
        intent.putExtra("Mode", mode);
        service.getApplicationContext().sendBroadcast(intent);
    }

    //Intent actions
    private enum INTENT_ACTIONS {

        /**
         * STACK Actions
         */
        ACTION_START_BEZIRK("START_BEZIRK", ACTION_TYPE.BEZIRK_STACK_ACTION),
        ACTION_STOP_BEZIRK("STOP_BEZIRK", ACTION_TYPE.BEZIRK_STACK_ACTION),
        ACTION_REBOOT("RESTART", ACTION_TYPE.BEZIRK_STACK_ACTION),
        ACTION_CLEAR_PERSISTENCE("ACTION_CLEAR_PERSISTENCE", ACTION_TYPE.BEZIRK_STACK_ACTION),
        ACTION_DIAG_PING("ACTION_DIAG_PING", ACTION_TYPE.BEZIRK_STACK_ACTION),
        ACTION_START_REST_SERVER("START_REST_SERVER", ACTION_TYPE.BEZIRK_STACK_ACTION),
        ACTION_STOP_REST_SERVER("STOP_REST_SERVER", ACTION_TYPE.BEZIRK_STACK_ACTION),

        /**
         * SEND Actions
         */
        ACTION_BEZIRK_PUSH_MULTICAST_STREAM("MULTICAST_STREAM", ACTION_TYPE.SEND_ACTION),
        ACTION_SERVICE_SEND_MULTICAST_EVENT("MULTICAST_EVENT", ACTION_TYPE.SEND_ACTION),
        ACTION_SERVICE_SEND_UNICAST_EVENT("UNICAST_EVENT", ACTION_TYPE.SEND_ACTION),
        ACTION_BEZIRK_PUSH_UNICAST_STREAM("UNICAST_STREAM", ACTION_TYPE.SEND_ACTION),

        /**
         * SERVICE Actions
         */
        ACTION_BEZIRK_REGISTER("REGISTER", ACTION_TYPE.SERVICE_ACTION),
        ACTION_SERVICE_DISCOVER("DISCOVER", ACTION_TYPE.SERVICE_ACTION),
        ACTION_BEZIRK_UNSUBSCRIBE("UNSUBSCRIBE", ACTION_TYPE.SERVICE_ACTION),
        ACTION_PIPE_REQUEST("PIPE_REQUEST", ACTION_TYPE.SERVICE_ACTION),
        ACTION_BEZIRK_SUBSCRIBE("SUBSCRIBE", ACTION_TYPE.SERVICE_ACTION),
        ACTION_BEZIRK_SETLOCATION("LOCATION", ACTION_TYPE.SERVICE_ACTION),

        /**
         * DEVICE Actions
         */
        ACTION_CHANGE_DEVICE_NAME("ACTION_CHANGE_DEVICE_NAME", ACTION_TYPE.DEVICE_ACTION),
        ACTION_CHANGE_DEVICE_TYPE("ACTION_CHANGE_DEVICE_TYPE", ACTION_TYPE.DEVICE_ACTION),
        ACTION_DEV_MODE_ON("ACTION_DEV_MODE_ON", ACTION_TYPE.DEVICE_ACTION),
        ACTION_DEV_MODE_OFF("ACTION_DEV_MODE_OFF", ACTION_TYPE.DEVICE_ACTION),
        ACTION_DEV_MODE_STATUS("ACTION_DEV_MODE_STATUS", ACTION_TYPE.DEVICE_ACTION);

        private final String message;
        private final ACTION_TYPE type;

        INTENT_ACTIONS(String actionName, ACTION_TYPE actionType) {
            message = actionName;
            type = actionType;
        }

        static INTENT_ACTIONS getActionUsingMessage(String actionMessage) {

            for (INTENT_ACTIONS intentAction : INTENT_ACTIONS.values()) {
                if (intentAction.message.equals(actionMessage) && ValidatorUtility.isObjectNotNull(intentAction.type)) {
                    return intentAction;
                }
            }
            return null;
        }

        private enum ACTION_TYPE {SERVICE_ACTION, SEND_ACTION, DEVICE_ACTION, BEZIRK_STACK_ACTION}

    }
}