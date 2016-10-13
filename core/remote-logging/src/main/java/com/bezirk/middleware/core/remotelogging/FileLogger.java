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
    private static final String RECIPIENT_MULTICAST_VALUE = "MULTI-CAST";
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.GERMANY);
    private File file = null;

    public FileLogger() {
        final String filePath = DataPathConfig.getDataPath() + File.separator + "log" + File.separator;

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss",
                Locale.getDefault());

        file = new File(filePath + dateFormat.format(new Date()) + ".txt");
    }

    public void handleLogMessage(RemoteLoggingMessage remoteLogMessage) {
        FileWriter fileOut = null;
        try {
            fileOut = new FileWriter(file);
            final BufferedWriter out = new BufferedWriter(fileOut);

            out.write(getSphereNameFromSphereId(remoteLogMessage.sphereName) + " " +
                    sdf.format(Long.valueOf(remoteLogMessage.timeStamp)) + " " +
                    remoteLogMessage.sender + " " +
                    getDeviceNameFromDeviceId(remoteLogMessage.recipient));

            out.close();
            fileOut.close();
        } catch (IOException e) {
            if (logger.isErrorEnabled()) logger.error("Failed to write remote logger message to " +
                    "file " + file.getAbsolutePath(), e);
        } finally {
            try {
                if (fileOut != null) fileOut.close();
            } catch (IOException ex) {
                logger.error("Failed to close remote logger FileOut after IOException", ex);
            }
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
