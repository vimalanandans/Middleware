package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.starter.BezirkConfig;

public abstract class Factory {
    private static Bezirk instance = null;

    /**
     * @return an object that implements Bezirk
     */
    public static Bezirk getInstance() {
        synchronized (Factory.class) {
            if (instance == null) {
                instance = new Proxy();
            }
            return instance;
        }
    }

    /**
     * TODO move this to java common
     */
    public static Bezirk getInstance(BezirkConfig bezirkConfig) {
        synchronized (Factory.class) {
            if (instance == null) {
                instance = new Proxy(bezirkConfig);
            }
            return instance;
        }
    }

}
