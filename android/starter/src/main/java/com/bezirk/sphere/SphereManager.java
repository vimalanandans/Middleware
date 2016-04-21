package com.bezirk.sphere;

import com.bezirk.sphere.impl.IUhuQRCode;

/**
 * Created by GUR1PI on 10/6/2014.
 */
public final class SphereManager {

    private static IUhuQRCode uhuQRCode;

    private SphereManager() {
        //To hide implicit public constructor
    }

    public static IUhuQRCode getUhuQRCode() {
        return uhuQRCode;
    }

    public static void setUhuQRCode(IUhuQRCode uhuQRCode) {
        SphereManager.uhuQRCode = uhuQRCode;
    }
}
