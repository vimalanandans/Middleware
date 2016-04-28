package com.bezirk.sphere.impl;

import java.awt.image.BufferedImage;

public interface BezirkQRCode {

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
