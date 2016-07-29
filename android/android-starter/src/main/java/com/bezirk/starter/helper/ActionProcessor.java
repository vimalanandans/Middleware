package com.bezirk.starter.helper;

import android.content.Intent;

import com.bezirk.actions.BezirkAction;
import com.bezirk.proxy.android.AndroidProxyServer;
import com.bezirk.sphere.api.DevMode;
import com.bezirk.starter.ActionCommands;
import com.bezirk.starter.MainService;
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

    public void processBezirkAction(Intent intent, MainService service, AndroidProxyServer ProxyService, MainStackHandler mainStackHandler) {
        BezirkAction intentAction = BezirkAction.getActionFromString(intent.getAction());

        if (ValidatorUtility.isObjectNotNull(intentAction)) {
            if (logger.isDebugEnabled())
                logger.debug("Received intent, action: {}", intentAction.getName());

            BezirkAction.ActionType actionType = intentAction.getType();

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
                case ZIRK_ACTION:
                    processServiceActions(intentAction, intent, ProxyService);
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

    private void processBezirkStackAction(MainService service, Intent intent, BezirkAction intentAction, MainStackHandler mainStackHandler) {
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
                if (logger.isWarnEnabled())
                    logger.warn("Received unknown intent action: {}", intent.getAction());
                break;
        }
    }

    private void processDeviceActions(BezirkAction intentAction, MainService service) {

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
                if (logger.isWarnEnabled())
                    logger.warn("Received unknown intent action: {}", intentAction.getName());
                break;
        }

    }

    private DevMode.Mode getDevMode() {
        return MainStackHandler.getDevMode() == null ? DevMode.Mode.OFF :
                MainStackHandler.getDevMode().getStatus();
    }

    private void processServiceActions(BezirkAction intentAction, Intent intent, AndroidProxyServer ProxyService) {
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
}