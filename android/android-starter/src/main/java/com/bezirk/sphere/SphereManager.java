package com.bezirk.sphere;

/**
 * Interface for controlling Sphere Manager
 * TODO: this shall be part of Sphere Manager component
 * */
public final class SphereManager {
    private static BezirkQRCode bezirkQRCode;

    private SphereManager() {
        //To hide implicit public constructor
    }

    public static BezirkQRCode getBezirkQRCode() {
        return bezirkQRCode;
    }

    public static void setBezirkQRCode(BezirkQRCode bezirkQRCode) {
        SphereManager.bezirkQRCode = bezirkQRCode;
    }
}
