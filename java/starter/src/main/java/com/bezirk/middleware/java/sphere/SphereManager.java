package com.bezirk.middleware.java.sphere;

public final class SphereManager {
    private static com.bezirk.middleware.java.sphere.impl.BezirkQRCode bezirkQRCode;

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private SphereManager() {

    }

    public static com.bezirk.middleware.java.sphere.impl.BezirkQRCode getBezirkQRCode() {
        return bezirkQRCode;
    }

    public static void setBezirkQRCode(com.bezirk.middleware.java.sphere.impl.BezirkQRCode bezirkQRCode) {
        SphereManager.bezirkQRCode = bezirkQRCode;
    }
}
