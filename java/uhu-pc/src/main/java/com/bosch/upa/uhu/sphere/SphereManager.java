package com.bosch.upa.uhu.sphere;

import com.bosch.upa.uhu.sphere.impl.IUhuQRCode;

/**
 * Created by GUR1PI on 10/6/2014.
 */
public final class SphereManager {

    private static IUhuQRCode uhuQRCode;
    
    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private SphereManager() {

    }

    public static IUhuQRCode getUhuQRCode() {
        return uhuQRCode;
    }

    public static void setUhuQRCode(IUhuQRCode uhuQRCode) {
        SphereManager.uhuQRCode = uhuQRCode;
    }
}
