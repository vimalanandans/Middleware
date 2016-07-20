package com.bezirk.sphere.impl;

import com.bezirk.comms.Comms;
import com.bezirk.devices.DeviceInterface;
import com.bezirk.datastorage.SpherePersistence;
import com.bezirk.datastorage.SphereRegistry;
import com.bezirk.sphere.api.SphereListener;
import com.bezirk.sphere.api.SphereConfig;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.util.ValidatorUtility;
import com.google.zxing.common.BitMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PCSphereServiceManager extends SphereServiceManager implements BezirkQRCode,
        SphereListener {

    private static final Logger logger = LoggerFactory.getLogger(PCSphereServiceManager.class);
    private final SphereUI sphereUI;

    public PCSphereServiceManager(CryptoEngine cryptoEngine,
                                  DeviceInterface upaDevice, SphereRegistry sphereRegistry) {
        super(cryptoEngine, upaDevice, sphereRegistry);
        sphereUI = new SphereUI();
    }

    @Override
    public String getDefaultSphereCode() {
        final String sphereId = getDefaultSphereId();
        if (sphereId != null) {
            return getShareCode(sphereId);
        }
        return null;
    }

    @Override
    public BufferedImage getQRCode() {
        BufferedImage image = null;
        final String sphereId = getDefaultSphereId();
        if (sphereId != null) {
            final BitMatrix matrix = getQRCodeMatrix(sphereId);
            if (matrix != null) {
                image = prepareImage(matrix);
            }
        }
        return image;
    }

    private BufferedImage prepareImage(final BitMatrix matrix) {
        BufferedImage image;
        image = new BufferedImage(matrix.getWidth(),
                matrix.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0 : 0xFFFFFF);
            }
        }
        return image;
    }

    public boolean saveQRCode(String filePath, String filePrefix) {
        final String fileName;

        if (ValidatorUtility.checkForString(filePath)) {
            final File folder = new File(filePath);

            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    logger.error("Failed to save QR code: {}", folder.getAbsolutePath());
                }
            }

            fileName = filePath + File.separator + filePrefix + "_scan_qr.jpg";
        } else {
            fileName = "scan_qr.jpg";
        }

        final BufferedImage img = getQRCode();

        final File qrFile = new File(fileName);
        try {
            ImageIO.write(img, "jpg", qrFile);
        } catch (IOException e) {
            logger.error("unable to write qr file into " + fileName, e);
            return false;
        }
        logger.info("qr code created in " + fileName);

        return true;
    }

    @Override
    public void onLeaveResponseReceived(String memberLeaveResponse) {
        // Nothing to be done

    }

    @Override
    public void onLeaveRequestReceived(String leaveRequest) {
        // Nothing to be done

    }

    @Override
    public void onSphereDiscovered(boolean status, String sphereId) {
        // Nothing to be done

    }

    @Override
    public void startCatchQRCodeRequest(String qrcodeString, String sphereId) {
        // Nothing to be done

    }

    @Override
    public void onCatchStatus(Status status, String message) {
        if (sphereUI != null) {
            sphereUI.displayStatus(message);
        }
    }

    @Override
    public void onShareStatus(Status status, String message) {
        // Nothing to be done

    }

    @Override
    public void onSphereCreateStatus(String sphereId, SphereCreateStatus status) {
        // Nothing to be done

    }

    public void initSphere(SpherePersistence spherePersistence, Comms comms, SphereConfig sphereConfig) {
        super.initSphere(spherePersistence, comms, this, sphereConfig);
    }

}
