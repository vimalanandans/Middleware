package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;

public abstract class Factory {
    /**
     * @return an object that implements Bezirk
     */
    public static Bezirk getInstance() {
        synchronized (Factory.class) {
            return new Proxy();
        }
    }
}
