package com.bezirk.sphere.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.bezirk.comms.IUhuComms;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.IUhuSphereListener;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.starter.UhuActionCommands;
import com.bezirk.starter.UhuPreferences;
import com.google.zxing.common.BitMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BezirkSphereForAndroid extends BezirkSphere implements IUhuSphereListener, IUhuQRCode {
    private static final Logger logger = LoggerFactory.getLogger(BezirkSphereForAndroid.class);

    private final Context applicationContext;
    private final UhuPreferences preferences;
    private ISphereConfig sphereConfig;

    public BezirkSphereForAndroid(CryptoEngine cryptoEngine, UPADeviceInterface upaDevice,
                                  SphereRegistry sphereRegistry, Context context, UhuPreferences preferences) {
        super(cryptoEngine, upaDevice, sphereRegistry);
        this.preferences = preferences;
        applicationContext = context;
    }

    public boolean initSphere(SpherePersistence spherePersistence, IUhuComms uhuComms) {
        initializeSphereConfig();
        if (sphereConfig == null) logger.error("SphereConfig is null");
        return super.initSphere(spherePersistence, uhuComms, this, sphereConfig);
    }

    private void initializeSphereConfig() {
        if (sphereConfig == null) {
            logger.info("Initializing the SphereConfig");
            sphereConfig = new SphereProperties(preferences);
            sphereConfig.init();
        }
    }

    /**
     * Generates the QR code for a sphere
     *
     * @param sphereId sphere Id for which the QR code needs to be generated
     * @return
     */
    @Override
    public Bitmap getQRCode(String sphereId) {

        Bitmap image = null;
        BitMatrix matrix = getQRCodeMatrix(sphereId);
        if (matrix != null) {
            image = Bitmap.createBitmap(matrix.getWidth(),
                    matrix.getHeight(), Bitmap.Config.ARGB_8888);

            for (int y = 0; y < matrix.getHeight(); y++) {
                for (int x = 0; x < matrix.getWidth(); x++) {
                    image.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        }
        return image;
    }

    @Override
    public Bitmap getQRCode(String sphereId, int width, int height) {
        if (width == -1 || height == -1) {
            return getQRCode(sphereId);
        }
        Bitmap image = null;
        BitMatrix matrix = getQRCodeMatrix(sphereId, width, height);
        if (matrix != null) {
            image = Bitmap.createBitmap(matrix.getWidth(),
                    matrix.getHeight(), Bitmap.Config.ARGB_8888);

            for (int y = 0; y < matrix.getHeight(); y++) {
                for (int x = 0; x < matrix.getWidth(); x++) {
                    image.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        }
        return image;
    }


    @Override
    public void onLeaveResponseReceived(String memberLeaveResponse) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLeaveRequestReceived(String leaveRequest) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCatchStatus(Status status, String message) {
        logger.debug("received info from listener, status: " + status.toString() + " message: " + message);
        sendIntent(status.toString(), message, UhuActionCommands.CMD_SPHERE_CATCH_STATUS);
    }

    @Override
    public void onShareStatus(Status status, String message) {
        logger.debug("received info from listener, status: " + status.toString() + " message: " + message);
        sendIntent(status.toString(), message, UhuActionCommands.CMD_SPHERE_SHARE_STATUS);
    }

    @Override
    public void onSphereCreateStatus(String sphereId, SphereCreateStatus status) {
        //Nothing to be done

    }

    @Override
    public void onSphereDiscovered(final boolean status, final String sphereId) {
        // TODO Auto-generated method stub
        logger.info("onSphereDiscovered status : " + sphereId);
        sendIntent(true, sphereId, UhuActionCommands.CMD_SPHERE_DISCOVERY_STATUS);

    }

    void sendIntent(String status, String message, String command) {
        Intent intent = new Intent();

        intent.setAction(UhuActionCommands.SPHERE_NOTIFICATION_ACTION);
        intent.putExtra(UhuActionCommands.UHU_ACTION_COMMANDS, command);
        intent.putExtra(UhuActionCommands.UHU_ACTION_COMMAND_STATUS, status);
        intent.putExtra(UhuActionCommands.UHU_ACTION_COMMAND_MESSAGE, message);

        // this sends only to the active activity
        if (applicationContext != null)
            applicationContext.sendBroadcast(intent);
    }

    void sendIntent(boolean status, String sphereId, String command) {
        Intent intent = new Intent();

        intent.setAction(UhuActionCommands.SPHERE_NOTIFICATION_ACTION);
        intent.putExtra(UhuActionCommands.UHU_ACTION_COMMANDS, command);
        intent.putExtra(UhuActionCommands.UHU_ACTION_COMMAND_STATUS, status);
        intent.putExtra(UhuActionCommands.UHU_ACTION_COMMAND_SPHERE_ID, sphereId);

        // this sends only to the active activity
        if (applicationContext != null)
            applicationContext.sendBroadcast(intent);
    }

}
