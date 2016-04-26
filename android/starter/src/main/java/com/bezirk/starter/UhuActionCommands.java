package com.bezirk.starter;

/**
 * Created by vnd2kor on 12/21/2014.
 * Bezirk Action Commands to send the notification to UI Via intents
 * At the moment this is specific to android. if this shall be
 */

public final class UhuActionCommands {

    public final static String SPHERE_NOTIFICATION_ACTION = "com.bosch.upa.uhu.spherenotfication";

    /**
     * generic parameters
     */
    public final static String UHU_ACTION_COMMANDS = "commands";

    public final static String UHU_ACTION_COMMAND_STATUS = "Status";

    public final static String UHU_ACTION_COMMAND_SPHERE_ID = "SphereId";

    public final static String UHU_ACTION_COMMAND_MESSAGE = "Message";
    /* ------------------------
    * Bezirk Control UI Action response Commands to UI
    * ------------------------
    */

    public final static String CMD_CHANGE_DEVICE_NAME_STATUS = "CMD_CHANGE_DEVICE_NAME_STATUS";

    public final static String CMD_CHANGE_DEVICE_TYPE_STATUS = "CMD_CHANGE_DEVICE_TYPE_STATUS";

    public final static String CMD_CHANGE_TYPE_STATUS = "CMD_CHANGE_TYPE_STATUS";

    public final static String CMD_CHANGE_SPHERE_NAME_STATUS = "CMD_CHANGE_SPHERE_NAME_STATUS";

    public final static String CMD_CHANGE_SPHERE_TYPE_STATUS = "CMD_CHANGE_SPHERE_TYPE_STATUS";

    public final static String CMD_CHANGE_DEVICE_LOCATION_STATUS = "CMD_CHANGE_DEVICE_LOCATION_STATUS";

    public final static String CMD_CLEAR_PERSISTENCE_STATUS = "CMD_CLEAR_PERSISTENCE_STATUS";

    public final static String CMD_DIAGNOSIS_STATUS = "CMD_DIAGNOSIS_STATUS";

    /*-----------------Development mode commands: start-----------------*/

    /* Used for sending current dev mode status to the UI */
    public final static String CMD_DEV_MODE_STATUS = "CMD_DEV_MODE_STATUS";

    /* Used for sending success/failure of turning ON development command to the UI */
    public final static String CMD_DEV_MODE_ON_STATUS = "CMD_DEV_MODE_ON_STATUS";

    /* Used for sending success/failure of turning OFF development command to the UI */
    public final static String CMD_DEV_MODE_OFF_STATUS = "CMD_DEV_MODE_OFF_STATUS";

    /*-----------------Development mode commands: end-----------------*/

    /*
     * ------------------------
     *
     * Bezirk sphere UI response Commands
     * ------------------------
     */
    public final static String CMD_SPHERE_CREATE_STATUS = "CMD_SPHERE_CREATE_STATUS";

    public final static String CMD_SPHERE_CATCH_STATUS = "CMD_SPHERE_CATCH_STATUS";

    public final static String CMD_SPHERE_SHARE_STATUS = "CMD_SPHERE_SHARE_STATUS";

    public final static String CMD_SPHERE_DISCOVERY_STATUS = "CMD_SPHERE_DISCOVERY_STATUS";

    private UhuActionCommands() {
        //Utility class. Hence hiding the default public constructor
    }

}
