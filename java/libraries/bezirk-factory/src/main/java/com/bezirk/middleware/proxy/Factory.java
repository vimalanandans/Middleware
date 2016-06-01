package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;

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

}
