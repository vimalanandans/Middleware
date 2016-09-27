package com.bezirk.middleware.core.remotelogging;

import com.bezirk.middleware.core.sphere.api.SphereAPI;
import com.bezirk.middleware.core.util.DataPathConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLogger {

    private static final Logger logger = LoggerFactory.getLogger(ReceiverQueueProcessor.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.GERMANY);
    private static final String RECIPIENT_MULTICAST_VALUE = "MULTI-CAST";

    private File file = null;

    public FileLogger() {
        Date date = new Date();
        String filePath;

        filePath = DataPathConfig.getDataPath() + File.separator + "log" + File.separator;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        file = new File(filePath + dateFormat.format(date) + ".txt");

    }

    public void handleLogMessage(RemoteLoggingMessage remoteLogMessage) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));

            out.write(getSphereNameFromSphereId(remoteLogMessage.sphereName) + " " + sdf.format(Long.valueOf(remoteLogMessage.timeStamp)) +
                    " " + remoteLogMessage.sender + " " + getDeviceNameFromDeviceId(remoteLogMessage.recipient));
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String getSphereNameFromSphereId(final String sphereId) {
        logger.debug("Getting the sphere name from the sphereId");
        final StringBuilder tempSphereName = new StringBuilder();
        //SphereAPI sphereAPI=new SphereServiceManager();
        SphereAPI sphereAPI = null;
        try {
            if (null != sphereAPI) {
                logger.debug("sphereAPI is not null");
                tempSphereName.append(sphereAPI.getSphere(sphereId).getSphereName());
            } else {
                logger.debug("sphereAPI is null");
            }
        } catch (NullPointerException ne) {
            logger.error("Error in fetching sphereName from sphere UI", ne);
            tempSphereName.append("Un-defined");
        }
        return tempSphereName.toString();
    }

    /**
     * Returns the DeviceName associated with the deviceId
     *
     * @param deviceId Device Id whose name is to be fetched
     * @return DeviceName if exists, null otherwise
     */
    private String getDeviceNameFromDeviceId(final String deviceId) {
        if (deviceId == null) {
            return RECIPIENT_MULTICAST_VALUE;
        }
        //SphereServiceAccess sphereServiceAccess = new SphereServiceManager();
        //final String tempDeviceName = sphereServiceAccess.getDeviceNameFromSphere(deviceId);
        //return (null == tempDeviceName) ? deviceId : tempDeviceName;
        return deviceId;
    }

}
