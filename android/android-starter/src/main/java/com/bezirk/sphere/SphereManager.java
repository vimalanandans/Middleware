package com.bezirk.sphere;

/**
 * Interface for controlling Sphere Manager
 * TODO: this shall be part of Sphere Manager component
 * */
public final class SphereManager {
    private static QRCode QRCode;

    private SphereManager() {
        //To hide implicit public constructor
    }

    public static QRCode getQRCode() {
        return QRCode;
    }

    public static void setQRCode(QRCode QRCode) {
        SphereManager.QRCode = QRCode;
    }
}
