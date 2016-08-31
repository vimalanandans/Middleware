package com.bezirk.controlui;

import com.bezirk.remotelogging.ReceiverQueueProcessor;
import com.bezirk.remotelogging.RemoteLoggingMessage;
import com.bezirk.sphere.api.SphereAPI;
import com.bezirk.sphere.api.SphereServiceAccess;
import com.bezirk.sphere.impl.SphereServiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bezirk.sphere.api.SphereAPI;
/**
 * Created by psg6kor on 8/8/2016.
 */
public class FileCreationHelper {

    private static final Logger logger = LoggerFactory.getLogger(ReceiverQueueProcessor.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.GERMANY);
    private static final String RECIPIENT_MULTICAST_VALUE = "MULTI-CAST";

    public void fileCreationOnTimeStamp(RemoteLoggingMessage remoteLogMessage)throws IOException{
        logger.debug("create file with timestamp and store the remotelogging message");
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
        File file = new File("C:/Users/psg6kor/DesktopdateFormat.format(date) + ".txt"") ;
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(getSphereNameFromSphereId(remoteLogMessage.sphereName) +" " +sdf.format(Long.valueOf(remoteLogMessage.timeStamp)) +
                " "+remoteLogMessage.sender + " "+getDeviceNameFromDeviceId(remoteLogMessage.recipient));
        out.close();
    }
    private String getSphereNameFromSphereId(final String sphereId) {
        logger.debug("Getting the sphere name from the sphereId");
        final StringBuilder tempSphereName = new StringBuilder();
        SphereAPI sphereAPI=new SphereServiceManager();
        try {
            if(null!=sphereAPI){
                logger.debug("sphereAPI is not null");
                tempSphereName.append(sphereAPI.getSphere(sphereId).getSphereName());
            }else{
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
        SphereServiceAccess sphereServiceAccess = new SphereServiceManager();
        final String tempDeviceName = sphereServiceAccess.getDeviceNameFromSphere(deviceId);
        // final String tempDeviceName = BezirkCompManager.getSphereForPubSubBroker() \
        //         .getDeviceNameFromSphere(deviceId);
        return (null == tempDeviceName) ? deviceId : tempDeviceName;
    }
}
