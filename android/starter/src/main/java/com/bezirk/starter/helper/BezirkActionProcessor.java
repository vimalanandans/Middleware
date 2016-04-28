package com.bezirk.starter.helper;

import android.content.Intent;

import com.bezirk.sphere.api.BezirkDevMode;
import com.bezirk.starter.MainService;
import com.bezirk.starter.BezirkActionCommands;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of processing intent action based on type.
 * Handles zirk actions, send actions, stack actions and bezirk device actions.
 * <p/>
 * Created by AJC6KOR on 9/8/2015.
 */
public final class BezirkActionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BezirkActionProcessor.class);

    private static final String CONTROL_UI_NOTIFICATION_ACTION = "com.bosch.upa.uhu.controluinotfication";
    private static final int START_CODE = 100;
    private static final int STOP_CODE = 101;

    /**
     * Process UhuAction based on action type.
     *
     * @param intent
     * @param service
     * @param bezirkServiceHelper
     * @param bezirkStackHandler
     */
    public void processBezirkAction(Intent intent, MainService service, BezirkServiceHelper bezirkServiceHelper, BezirkStackHandler bezirkStackHandler) {

        INTENT_ACTIONS intentAction = INTENT_ACTIONS.getActionUsingMessage(intent.getAction());

        if (BezirkValidatorUtility.isObjectNotNull(intentAction)) {
            logger.info("Intent Action > {}", intentAction.message);

            INTENT_ACTIONS.ACTION_TYPE actionType = intentAction.type;

            switch (actionType) {

                case BEZIRK_STACK_ACTION:
                    processBezirkStackAction(service, intent, intentAction, bezirkStackHandler);
                    break;
                case DEVICE_ACTION:
                    processDeviceActions(intentAction, service);
                    break;
                case SEND_ACTION:
                    processSendActions(intentAction, intent, bezirkServiceHelper);
                    break;
                case SERVICE_ACTION:
                    processServiceActions(intentAction, intent, service, bezirkServiceHelper);
                    break;
                default:
                    logger.warn("Received unknown intent action: " + intentAction.message);
                    break;
            }
        } else {

            logger.warn("Received unknown intent action: " + intent.getAction());
        }

    }

    private void processBezirkStackAction(MainService service, Intent intent, INTENT_ACTIONS intentAction, BezirkStackHandler bezirkStackHandler) {
        switch (intentAction) {
            case ACTION_START_BEZIRK:
                bezirkStackHandler.startStack(service);
                break;
            case ACTION_STOP_BEZIRK:
                bezirkStackHandler.stopStack(service);
                break;
            case ACTION_REBOOT:
                bezirkStackHandler.reboot(service);
                break;
            case ACTION_CLEAR_PERSISTENCE:
                bezirkStackHandler.clearPersistence(service);
                break;
            case ACTION_DIAG_PING:
                bezirkStackHandler.diagPing(intent);
                break;
            case ACTION_START_REST_SERVER:
                bezirkStackHandler.startStopRestServer(START_CODE);
                break;
            case ACTION_STOP_REST_SERVER:
                bezirkStackHandler.startStopRestServer(STOP_CODE);
                break;
            default:
                logger.warn("Received unknown intent action: " + intentAction.message);
                break;
        }
    }

    private void processDeviceActions(INTENT_ACTIONS intentAction, MainService service) {

        switch (intentAction) {

            case ACTION_CHANGE_DEVICE_NAME:
                sendIntent(BezirkActionCommands.CMD_CHANGE_DEVICE_NAME_STATUS, true, service);
                break;
            case ACTION_CHANGE_DEVICE_TYPE:
                sendIntent(BezirkActionCommands.CMD_CHANGE_DEVICE_TYPE_STATUS, true, service);
                break;
            case ACTION_DEV_MODE_ON:
                sendIntent(BezirkActionCommands.CMD_DEV_MODE_ON_STATUS, BezirkStackHandler.getDevMode().switchMode(BezirkDevMode.Mode.ON), service);
                break;
            case ACTION_DEV_MODE_OFF:
                sendIntent(BezirkActionCommands.CMD_DEV_MODE_OFF_STATUS, BezirkStackHandler.getDevMode().switchMode(BezirkDevMode.Mode.OFF), service);
                break;
            case ACTION_DEV_MODE_STATUS:
                BezirkDevMode.Mode mode = getDevMode();
                sendIntent(BezirkActionCommands.CMD_DEV_MODE_STATUS, mode, service);
                break;
            default:
                logger.warn("Received unknown intent action: {}", intentAction.message);
                break;
        }

    }

    private BezirkDevMode.Mode getDevMode() {
        BezirkDevMode.Mode mode;
        if (BezirkStackHandler.getDevMode() == null) {
            // if user wifi supplicant status is Disconnected and on Init he clicks on DeviceControl, devMode will be null.
            mode = BezirkDevMode.Mode.OFF;
        } else {
            mode = BezirkStackHandler.getDevMode().getStatus();
        }
        return mode;
    }

    private void processServiceActions(INTENT_ACTIONS intentAction, Intent intent, MainService service, BezirkServiceHelper bezirkServiceHelper) {

        switch (intentAction) {

            case ACTION_BEZIRK_REGISTER:
                bezirkServiceHelper.registerService(intent);
                break;
            case ACTION_BEZIRK_SUBSCRIBE:
                bezirkServiceHelper.subscribeService(intent);
                break;
            case ACTION_BEZIRK_SETLOCATION:
                bezirkServiceHelper.setLocation(intent);
                break;
            case ACTION_SERVICE_DISCOVER:
                bezirkServiceHelper.discoverService(intent);
                break;
            case ACTION_BEZIRK_UNSUBSCRIBE:
                bezirkServiceHelper.unsubscribeService(intent);
                break;
            case ACTION_PIPE_REQUEST:
                service.processPipeRequest(intent);
                break;
            default:
                logger.warn("Received unknown intent action: " + intentAction.message);
                break;
        }
    }

    private void processSendActions(INTENT_ACTIONS intentAction, Intent intent, BezirkServiceHelper bezirkServiceHelper) {

        switch (intentAction) {
            case ACTION_SERVICE_SEND_MULTICAST_EVENT:
                bezirkServiceHelper.sendMulticastEvent(intent);
                break;
            case ACTION_SERVICE_SEND_UNICAST_EVENT:
                bezirkServiceHelper.sendUnicastEvent(intent);
                break;
            case ACTION_BEZIRK_PUSH_UNICAST_STREAM:
                bezirkServiceHelper.sendUnicastStream(intent);
                break;
            case ACTION_BEZIRK_PUSH_MULTICAST_STREAM:
                bezirkServiceHelper.sendMulticastStream(intent);
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

    private void sendIntent(String command, BezirkDevMode.Mode mode, MainService service) {
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
                if (intentAction.message.equals(actionMessage) && BezirkValidatorUtility.isObjectNotNull(intentAction.type)) {
                    return intentAction;
                }
            }
            return null;
        }

        private enum ACTION_TYPE {SERVICE_ACTION, SEND_ACTION, DEVICE_ACTION, BEZIRK_STACK_ACTION}

    }
}