package com.bezirk.sphere;

import android.graphics.Bitmap;

/**
 * Created by Rishabh Gulati on 12/5/2014.
 * Modified by Vijet Badigannavar on 6/16/2015
 * Not used since it is project specific implementation
 */
public interface QRCode {
    public Bitmap getQRCode(String sphereId);

    /**
     * Retrieves the QRCode with specific dimensions. This method is useful to show QRCode of different sizes on different devices.
     *
     * @param sphereId Name of the sphere that will be imprinted in the QRCode image
     * @param width    width of the image
     * @param height   height of the image
     * @return Bitmap containing the QRCode imprinted with the sphereId
     */
    public Bitmap getQRCode(String sphereId, int width, int height);
}
