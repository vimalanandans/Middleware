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
package com.bezirk.middleware.core.actions;

public enum BezirkAction {
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

    ACTION_ZIRK_RECEIVE_EVENT("RECEIVE_EVENT", ActionType.RECEIVE_ACTION),
    ACTION_ZIRK_RECEIVE_STREAM("RECEIVE_STREAM", ActionType.RECEIVE_ACTION),
    ACTION_ZIRK_RECEIVE_STREAM_STATUS("RECEIVE_STREAM_STATUS", ActionType.RECEIVE_ACTION),
    ACTION_ZIRK_SEND_MULTICAST_EVENT("MULTICAST_EVENT", ActionType.SEND_ACTION),
    ACTION_ZIRK_SEND_UNICAST_EVENT("UNICAST_EVENT", ActionType.SEND_ACTION),
    ACTION_BEZIRK_PUSH_UNICAST_STREAM("UNICAST_STREAM", ActionType.SEND_ACTION),

    //STREAM EXTRA ACTION
    ACTION_BEZIRK_PUSH_UNICAST_STREAM_ENCRYPT("UNICAST_STREAM_ENCRYPT", ActionType.SEND_ACTION),
    ACTION_BEZIRK_PUSH_UNICAST_STREAM_INCREMENTAL("UNICAST_STREAM_INCREMENTAL", ActionType.SEND_ACTION),
    ACTION_BEZIRK_PUSH_UNICAST_STREAM_FILE("UNICAST_STREAM_FILE", ActionType.SEND_ACTION),

    ACTION_BEZIRK_REGISTER("REGISTER", ActionType.ZIRK_ACTION),
    ACTION_BEZIRK_UNSUBSCRIBE("UNSUBSCRIBE", ActionType.ZIRK_ACTION),
    ACTION_BEZIRK_SUBSCRIBE("SUBSCRIBE", ActionType.ZIRK_ACTION),
    ACTION_BEZIRK_SET_LOCATION("LOCATION", ActionType.ZIRK_ACTION),
    ACTION_BEZIRK_GET_IDENTITY_MANAGER("GET_IDENTITY_MANAGER", ActionType.ZIRK_ACTION),

    ACTION_CHANGE_DEVICE_NAME("ACTION_CHANGE_DEVICE_NAME", ActionType.DEVICE_ACTION),
    ACTION_CHANGE_DEVICE_TYPE("ACTION_CHANGE_DEVICE_TYPE", ActionType.DEVICE_ACTION),
    ACTION_DEV_MODE_ON("ACTION_DEV_MODE_ON", ActionType.DEVICE_ACTION),
    ACTION_DEV_MODE_OFF("ACTION_DEV_MODE_OFF", ActionType.DEVICE_ACTION),
    ACTION_DEV_MODE_STATUS("ACTION_DEV_MODE_STATUS", ActionType.DEVICE_ACTION),

    KEY_PIPE_NAME("KEY_PIPE_NAME", ActionType.PIPE_ACTION),
    KEY_PIPE_REQ_ID("KEY_PIPE_REQ_ID", ActionType.PIPE_ACTION),
    KEY_PIPE_SPHEREID("KEY_PIPE_SPHEREID", ActionType.PIPE_ACTION),
    KEY_SENDER_ZIRK_ID("KEY_SENDER_ZIRK_ID", ActionType.PIPE_ACTION),

    ACTION_REST_START_BEZIRK("ACTION_REST_START_BEZIRK", ActionType.REST_ACTION),
    ACTION_REST_STOP_BEZIRK("ACTION_REST_STOP_BEZIRK", ActionType.REST_ACTION);


    private final String name;
    private final ActionType type;

    BezirkAction(String actionName, ActionType actionType) {
        name = actionName;
        type = actionType;
    }

    public static BezirkAction getActionFromString(String actionName) {
        for (BezirkAction intentAction : BezirkAction.values()) {
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

    public enum ActionType {ZIRK_ACTION, SEND_ACTION, RECEIVE_ACTION, DEVICE_ACTION, BEZIRK_STACK_ACTION, PIPE_ACTION, REST_ACTION}
}
