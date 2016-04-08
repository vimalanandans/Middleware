package com.bosch.upa.uhu.sphere.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.api.IUhuSphereListener;
import com.bosch.upa.uhu.sphere.security.CryptoEngine;
import com.bosch.upa.uhu.util.UhuValidatorUtility;
import com.google.zxing.common.BitMatrix;

/**
 * Created by GUR1PI on 8/9/2014.
 */
public class UhuSphereForPC extends UhuSphere implements IUhuQRCode,
        IUhuSphereListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UhuSphereForPC.class);
    private final SphereUI sphereUI;

    public UhuSphereForPC(CryptoEngine cryptoEngine,
            UPADeviceInterface upaDevice, SphereRegistry sphereRegistry) {
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
        String fileName = null;

        if (UhuValidatorUtility.checkForString(filePath)) {
            final File folder = new File(filePath);

            if (!folder.exists()) {
                folder.mkdir();
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
            LOGGER.error("unable to write qr file into " + fileName,e);
            return false;
        }
        LOGGER.info("qr code created in " + fileName);

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

	public void initSphere(ISpherePersistence spherePersistence, IUhuComms uhuComms, ISphereConfig sphereConfig) {
		super.initSphere(spherePersistence, uhuComms, this, sphereConfig);
	}

}
