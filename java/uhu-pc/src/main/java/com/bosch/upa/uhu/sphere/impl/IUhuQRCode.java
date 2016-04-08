package com.bosch.upa.uhu.sphere.impl;

import java.awt.image.BufferedImage;

/**
 * Created by Rishabh Gulati on 12/5/2014.
 */
public interface IUhuQRCode {

    /**
     * Currently implemented for only the default sphere. When the sphereId is
     * accessible though a UI, the sphereId would be taken as parameter like in
     * Android
     * 
     * @return
     */
    BufferedImage getQRCode();

    String getDefaultSphereCode();

    void startCatchQRCodeRequest(String qrcodeString, String sphereId);
}
