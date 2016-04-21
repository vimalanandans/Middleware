package com.bezirk.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.starter.UhuConfig;


public abstract class Factory {
    private static Bezirk instance = null;

    /**
     * @return an object that implements UhuAPI
     */
    public static Bezirk getInstance() {
        synchronized (Factory.class) {
            if (instance == null) {
                instance = (Bezirk) new Proxy();
            }
            return instance;
        }
    }

    /**
     * TODO move this to java common
     */
    public static Bezirk getInstance(UhuConfig uhuConfig) {
        synchronized (Factory.class) {
            if (instance == null) {
                instance = (Bezirk) new Proxy(uhuConfig);
            }
            return instance;
        }
    }

}
