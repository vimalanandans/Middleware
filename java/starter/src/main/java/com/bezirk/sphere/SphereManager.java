package com.bezirk.sphere;

/**
 * Created by GUR1PI on 10/6/2014.
 */
public final class SphereManager {

    private static com.bezirk.sphere.impl.IUhuQRCode uhuQRCode;

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private SphereManager() {

    }

    public static com.bezirk.sphere.impl.IUhuQRCode getUhuQRCode() {
        return uhuQRCode;
    }

    public static void setUhuQRCode(com.bezirk.sphere.impl.IUhuQRCode uhuQRCode) {
        SphereManager.uhuQRCode = uhuQRCode;
    }
}
