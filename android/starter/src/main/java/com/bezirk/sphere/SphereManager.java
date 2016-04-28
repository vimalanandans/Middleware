package com.bezirk.sphere;

import com.bezirk.sphere.impl.BezirkQRCode;

/**
 * Created by GUR1PI on 10/6/2014.
 */
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
