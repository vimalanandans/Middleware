package com.bezirk.test.sphere.testUtilities;

import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.IUhuDevMode.Mode;

public class SpherePropertiesMock implements ISphereConfig {

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public Mode getMode() {
        return Mode.OFF;
    }

    @Override
    public String getSphereName() {
        return "DevSphere";
    }

    @Override
    public String getSphereId() {
        return "DevSphereId";
    }

    @Override
    public byte[] getSphereKey() {
        return new String("eCUSEA+QRRJnuj9yAe8QAQ==").getBytes();
    }

    @Override
    public String getDefaultSphereName() {
        return "DefSphere";
    }

    @Override
    public boolean setDefaultSphereName(String name) {
        return false;
    }

    @Override
    public boolean setMode(Mode mode) {
        return true;
    }

}
