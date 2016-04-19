package com.bezirk.proxy.android;

import android.content.Context;

import com.bezirk.middleware.Bezirk;


public final class Factory {
    private static Proxy instance;

    private Factory(){
        //To hide public constructor
    }
    public static final Bezirk getInstance(Context context) {
        synchronized (Factory.class) {
            if (instance == null) {
                instance = new Proxy(context);
            }
            return instance;
        }
    }
}
