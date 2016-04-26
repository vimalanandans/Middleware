package com.bezirk.actions;

public class UhuActions {

    //------------------------
    // Component constants
    //------------------------

    public final static String COMPONENT_UHU_PKG = "UHU_PKG";
    public final static String COMPONENT_UHU_CLASS = "UHU_CLASS";

    //------------------------
    // Action name constants
    //------------------------
    //------------------------
    // Uhu Pipe Actions
    //------------------------
    public final static String ACTION_PIPE_REQUEST = "PIPE_REQUEST";

    //------------------------
    // Uhu Control Actions
    //------------------------
    public final static String ACTION_START_UHU = "START_UHU";

    public final static String ACTION_STOP_UHU = "STOP_UHU";

    public final static String ACTION_REST_START_UHU = "START_REST_SERVER";

    public final static String ACTION_REST_STOP_UHU = "STOP_REST_SERVER";

    public final static String ACTION_CHANGE_DEVICE_TYPE = "ACTION_CHANGE_DEVICE_TYPE";

    public final static String ACTION_CHANGE_DEVICE_NAME = "ACTION_CHANGE_DEVICE_NAME";

    public final static String ACTION_CHANGE_SPHERE_NAME = "ACTION_CHANGE_SPHERE_NAME";

    public final static String ACTION_CHANGE_SPHERE_TYPE = "ACTION_CHANGE_SPHERE_TYPE";

    public final static String ACTION_CHANGE_DEVICE_LOCATION = "ACTION_CHANGE_DEVICE_LOCATION";

    public final static String ACTION_CLEAR_PERSISTENCE = "ACTION_CLEAR_PERSISTENCE";

    public final static String ACTION_DIAG_UHU = "ACTION_DIAG_UHU";

    public final static String ACTION_DEV_MODE_ON = "ACTION_DEV_MODE_ON";

    public final static String ACTION_DEV_MODE_OFF = "ACTION_DEV_MODE_OFF";

    public final static String ACTION_DEV_MODE_STATUS = "ACTION_DEV_MODE_STATUS";

    //-----------------------------
    // Intent "extra" key constants
    //-----------------------------

    public final static String KEY_SENDER_ZIRK_ID = "SENDER_ZIRK_ID";
    public final static String KEY_PIPE_REQ_ID = "PIPE_REQ_ID";
    public final static String KEY_PIPE_NAME = "PIPE_NAME";
    public final static String KEY_PIPE_URI = "PIPE_URI";
    public final static String KEY_PIPE_TYPE = "PIPE_TYPE";
    public final static String KEY_PIPE_CLASS = "PIPE_CLASS";
    public final static String KEY_PIPE_POLICY_IN = "PIPE_POLICY_IN";
    public final static String KEY_PIPE_POLICY_OUT = "PIPE_POLICY_OUT";
    public final static String KEY_PIPE_SPHEREID = "PIPE_SPHEREID";
    public final static String KEY_PIPE = "PIPE_ONLY";
}
