package com.bezirk.starter.helper;

import android.content.Intent;

import com.bezirk.sphere.api.IUhuDevMode;
import com.bezirk.starter.MainService;
import com.bezirk.starter.UhuActionCommands;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of processing intent action based on type.
 * Handles zirk actions, send actions, stack actions and uhu device actions.
 * <p/>
 * Created by AJC6KOR on 9/8/2015.
 */
public final class UhuActionProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UhuActionProcessor.class);
    private static final String CONTROL_UI_NOTIFICATION_ACTION = "com.bosch.upa.uhu.controluinotfication";
    private static final int START_CODE = 100;
    private static final int STOP_CODE = 101;

    /**
     * Process UhuAction based on action type.
     *
     * @param intent
     * @param service
     * @param uhuServiceHelper
     * @param uhuStackHandler
     */
    public void processUhuAction(Intent intent, MainService service, UhuServiceHelper uhuServiceHelper, UhuStackHandler uhuStackHandler) {

        INTENT_ACTIONS intentAction = INTENT_ACTIONS.getActionUsingMessage(intent.getAction());

        if (BezirkValidatorUtility.isObjectNotNull(intentAction)) {

            LOGGER.info("Intent Action > " + intentAction.message);

            INTENT_ACTIONS.ACTION_TYPE actionType = intentAction.type;

            switch (actionType) {

                case UHU_STACK_ACTION:
                    processUhuStackAction(service, intent, intentAction, uhuStackHandler);
                    break;
                case DEVICE_ACTION:
                    processDeviceActions(intentAction, service);
                    break;
                case SEND_ACTION:
                    processSendActions(intentAction, intent, uhuServiceHelper);
                    break;
                case SERVICE_ACTION:
                    processServiceActions(intentAction, intent, service, uhuServiceHelper);
                    break;
                default:
                    LOGGER.warn("Received unknown intent action: " + intentAction.message);
                    break;
            }
        } else {

            LOGGER.warn("Received unknown intent action: " + intent.getAction());
        }

    }

    private void processUhuStackAction(MainService service, Intent intent, INTENT_ACTIONS intentAction, UhuStackHandler uhuStackHandler) {
        switch (intentAction) {
            case ACTION_START_UHU:
                uhuStackHandler.startStack(service);
                break;
            case ACTION_STOP_UHU:
                uhuStackHandler.stopStack(service);
                break;
            case ACTION_REBOOT:
                uhuStackHandler.reboot(service);
                break;
            case ACTION_CLEAR_PERSISTENCE:
                uhuStackHandler.clearPersistence(service);
                break;
            case ACTION_DIAG_PING:
                uhuStackHandler.diagPing(intent);
                break;
            case ACTION_START_REST_SERVER:
                uhuStackHandler.startStopRestServer(START_CODE);
                break;
            case ACTION_STOP_REST_SERVER:
                uhuStackHandler.startStopRestServer(STOP_CODE);
                break;
            default:
                LOGGER.warn("Received unknown intent action: " + intentAction.message);
                break;
        }
    }

    private void processDeviceActions(INTENT_ACTIONS intentAction, MainService service) {

        switch (intentAction) {

            case ACTION_CHANGE_DEVICE_NAME:
                sendIntent(UhuActionCommands.CMD_CHANGE_DEVICE_NAME_STATUS, true, service);
                break;
            case ACTION_CHANGE_DEVICE_TYPE:
                sendIntent(UhuActionCommands.CMD_CHANGE_DEVICE_TYPE_STATUS, true, service);
                break;
            case ACTION_DEV_MODE_ON:
                sendIntent(UhuActionCommands.CMD_DEV_MODE_ON_STATUS, UhuStackHandler.getDevMode().switchMode(IUhuDevMode.Mode.ON), service);
                break;
            case ACTION_DEV_MODE_OFF:
                sendIntent(UhuActionCommands.CMD_DEV_MODE_OFF_STATUS, UhuStackHandler.getDevMode().switchMode(IUhuDevMode.Mode.OFF), service);
                break;
            case ACTION_DEV_MODE_STATUS:
                IUhuDevMode.Mode mode = null;
                mode = getDevMode();
                sendIntent(UhuActionCommands.CMD_DEV_MODE_STATUS, mode, service);
                break;
            default:
                LOGGER.warn("Received unknown intent action: " + intentAction.message);
                break;
        }

    }

    private IUhuDevMode.Mode getDevMode() {
        IUhuDevMode.Mode mode;
        if (UhuStackHandler.getDevMode() == null) {
            // if user wifi supplicant status is Disconnected and on Init he clicks on DeviceControl, devMode will be null.
            mode = IUhuDevMode.Mode.OFF;
        } else {
            mode = UhuStackHandler.getDevMode().getStatus();
        }
        return mode;
    }

    private void processServiceActions(INTENT_ACTIONS intentAction, Intent intent, MainService service, UhuServiceHelper uhuServiceHelper) {

        switch (intentAction) {

            case ACTION_UHU_REGISTER:
                uhuServiceHelper.registerService(intent);
                break;
            case ACTION_UHU_SUBSCRIBE:
                uhuServiceHelper.subscribeService(intent);
                break;
            case ACTION_UHU_SETLOCATION:
                uhuServiceHelper.setLocation(intent);
                break;
            case ACTION_SERVICE_DISCOVER:
                uhuServiceHelper.discoverService(intent);
                break;
            case ACTION_UHU_UNSUBSCRIBE:
                uhuServiceHelper.unsubscribeService(intent);
                break;
            case ACTION_PIPE_REQUEST:
                service.processPipeRequest(intent);
                break;
            default:
                LOGGER.warn("Received unknown intent action: " + intentAction.message);
                break;
        }
    }

    private void processSendActions(INTENT_ACTIONS intentAction, Intent intent, UhuServiceHelper uhuServiceHelper) {

        switch (intentAction) {
            case ACTION_SERVICE_SEND_MULTICAST_EVENT:
                uhuServiceHelper.sendMulticastEvent(intent);
                break;
            case ACTION_SERVICE_SEND_UNICAST_EVENT:
                uhuServiceHelper.sendUnicastEvent(intent);
                break;
            case ACTION_UHU_PUSH_UNICAST_STREAM:
                uhuServiceHelper.sendUnicastStream(intent);
                break;
            case ACTION_UHU_PUSH_MULTICAST_STREAM:
                uhuServiceHelper.sendMulticastStream(intent);
                break;
            default:
                LOGGER.warn("Received unknown intent action: " + intentAction.message);
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

    private void sendIntent(String command, IUhuDevMode.Mode mode, MainService service) {
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
        ACTION_START_UHU("START_UHU", ACTION_TYPE.UHU_STACK_ACTION),
        ACTION_STOP_UHU("STOP_UHU", ACTION_TYPE.UHU_STACK_ACTION),
        ACTION_REBOOT("RESTART", ACTION_TYPE.UHU_STACK_ACTION),
        ACTION_CLEAR_PERSISTENCE("ACTION_CLEAR_PERSISTENCE", ACTION_TYPE.UHU_STACK_ACTION),
        ACTION_DIAG_PING("ACTION_DIAG_PING", ACTION_TYPE.UHU_STACK_ACTION),
        ACTION_START_REST_SERVER("START_REST_SERVER", ACTION_TYPE.UHU_STACK_ACTION),
        ACTION_STOP_REST_SERVER("STOP_REST_SERVER", ACTION_TYPE.UHU_STACK_ACTION),

        /**
         * SEND Actions
         */
        ACTION_UHU_PUSH_MULTICAST_STREAM("MULTICAST_STREAM", ACTION_TYPE.SEND_ACTION),
        ACTION_SERVICE_SEND_MULTICAST_EVENT("MULTICAST_EVENT", ACTION_TYPE.SEND_ACTION),
        ACTION_SERVICE_SEND_UNICAST_EVENT("UNICAST_EVENT", ACTION_TYPE.SEND_ACTION),
        ACTION_UHU_PUSH_UNICAST_STREAM("UNICAST_STREAM", ACTION_TYPE.SEND_ACTION),

        /**
         * SERVICE Actions
         */
        ACTION_UHU_REGISTER("REGISTER", ACTION_TYPE.SERVICE_ACTION),
        ACTION_SERVICE_DISCOVER("DISCOVER", ACTION_TYPE.SERVICE_ACTION),
        ACTION_UHU_UNSUBSCRIBE("UNSUBSCRIBE", ACTION_TYPE.SERVICE_ACTION),
        ACTION_PIPE_REQUEST("PIPE_REQUEST", ACTION_TYPE.SERVICE_ACTION),
        ACTION_UHU_SUBSCRIBE("SUBSCRIBE", ACTION_TYPE.SERVICE_ACTION),
        ACTION_UHU_SETLOCATION("LOCATION", ACTION_TYPE.SERVICE_ACTION),

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

        private enum ACTION_TYPE {SERVICE_ACTION, SEND_ACTION, DEVICE_ACTION, UHU_STACK_ACTION}

    }
}