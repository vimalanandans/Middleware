package com.bosch.upa.uhu.sphere;

import com.bosch.upa.uhu.sphere.impl.IUhuQRCode;

/**
* Created by GUR1PI on 10/6/2014.
*/
public final class SphereManager {

    private SphereManager(){
        //To hide implicit public constructor
    }

    private static IUhuQRCode uhuQRCode;

    public static IUhuQRCode getUhuQRCode() {
        return uhuQRCode;
    }

    public static void setUhuQRCode(IUhuQRCode uhuQRCode) {
        SphereManager.uhuQRCode = uhuQRCode;
    }
}
