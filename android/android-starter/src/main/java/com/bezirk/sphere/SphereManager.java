package com.bezirk.sphere;

import com.bezirk.sphere.impl.BezirkQRCode;

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
