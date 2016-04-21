package com.bezirk.commstest.ui;

/**
 * @author MCA7KOR
 */
public final class CommsTestConstants {

    // String constants used in the UI

    public static final String IMAGE_PATH1 = "/settings.png";
    public static final String IMAGE_PATH2 = "/info.png";

    public static final String DEVICE_NAME_LABEL = "Device Name :";
    public static final String SENDING_PORT_LABEL = "Sending Port :";
    public static final String RECEIVING_PORT_LABEL = "Receiving Port :";

    public static final String DEFAULT_DEVICE_NAME = "DEVICE-1";
    public static final int DEFAULT_MULTICAST_SENDING_PORT = 1234;
    public static final int DEFAULT_MULTICAST_RECEIVING_PORT = 1234;
    public static final int DEFAULT_UNICAST_SENDING_PORT = 1235;
    public static final int DEFAULT_UNICAST_RECEIVING_PORT = 1235;
    public static final int DEFAULT_TIMER_VALUE = 10000;

    public static final String SETTINGS_DIALOG_TITLE = "UHU-COMMS-CONFIGURATION";
    public static final String HINT_DIALOG_TITLE = "DIAGNOSIS HINTS!!!";

    public static final String START_BUTTON_LABEL = "START";
    public static final String STOP_BUTTON_LABEL = "STOP";
    public static final String CLEAR_BUTTON_LABEL = "CLEAR";

    public static final String STATUS_LABEL = "STATUS";

    /* Utility Class. All variables are static. Adding private constructor to suppress PMD warnings.*/
    private CommsTestConstants() {
    }

}
