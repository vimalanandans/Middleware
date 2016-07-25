package com.bezirk.actions;

public enum BezirkActions {
    // actionName, type
    ACTION_START_BEZIRK("START_BEZIRK", ActionType.BEZIRK_STACK_ACTION),
    ACTION_STOP_BEZIRK("STOP_BEZIRK", ActionType.BEZIRK_STACK_ACTION),
    ACTION_REBOOT("RESTART", ActionType.BEZIRK_STACK_ACTION),
    ACTION_CLEAR_PERSISTENCE("ACTION_CLEAR_PERSISTENCE", ActionType.BEZIRK_STACK_ACTION),
    ACTION_DIAG_PING("ACTION_DIAG_PING", ActionType.BEZIRK_STACK_ACTION),
    ACTION_START_REST_SERVER("START_REST_SERVER", ActionType.BEZIRK_STACK_ACTION),
    ACTION_STOP_REST_SERVER("STOP_REST_SERVER", ActionType.BEZIRK_STACK_ACTION),
    ACTION_CHANGE_SPHERE_TYPE("ACTION_CHANGE_SPHERE_TYPE", ActionType.BEZIRK_STACK_ACTION),
    ACTION_CHANGE_SPHERE_NAME("ACTION_CHANGE_SPHERE_NAME", ActionType.BEZIRK_STACK_ACTION),

    ACTION_SERVICE_SEND_MULTICAST_EVENT("MULTICAST_EVENT", ActionType.SEND_ACTION),
    ACTION_SERVICE_SEND_UNICAST_EVENT("UNICAST_EVENT", ActionType.SEND_ACTION),
    ACTION_BEZIRK_PUSH_UNICAST_STREAM("UNICAST_STREAM", ActionType.SEND_ACTION),

    ACTION_BEZIRK_REGISTER("REGISTER", ActionType.ZIRK_ACTION),
    ACTION_BEZIRK_UNSUBSCRIBE("UNSUBSCRIBE", ActionType.ZIRK_ACTION),
    ACTION_BEZIRK_SUBSCRIBE("SUBSCRIBE", ActionType.ZIRK_ACTION),
    ACTION_BEZIRK_SET_LOCATION("LOCATION", ActionType.ZIRK_ACTION),

    ACTION_CHANGE_DEVICE_NAME("ACTION_CHANGE_DEVICE_NAME", ActionType.DEVICE_ACTION),
    ACTION_CHANGE_DEVICE_TYPE("ACTION_CHANGE_DEVICE_TYPE", ActionType.DEVICE_ACTION),
    ACTION_DEV_MODE_ON("ACTION_DEV_MODE_ON", ActionType.DEVICE_ACTION),
    ACTION_DEV_MODE_OFF("ACTION_DEV_MODE_OFF", ActionType.DEVICE_ACTION),
    ACTION_DEV_MODE_STATUS("ACTION_DEV_MODE_STATUS", ActionType.DEVICE_ACTION);

    private final String name;
    private final ActionType type;

    BezirkActions(String actionName, ActionType actionType) {
        name = actionName;
        type = actionType;
    }

    public static BezirkActions getActionFromString(String actionName) {
        for (BezirkActions intentAction : BezirkActions.values()) {
            if (intentAction.name.equals(actionName) && intentAction.type != null) {
                return intentAction;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public ActionType getType() {
        return type;
    }

    public enum ActionType {ZIRK_ACTION, SEND_ACTION, DEVICE_ACTION, BEZIRK_STACK_ACTION}
}
