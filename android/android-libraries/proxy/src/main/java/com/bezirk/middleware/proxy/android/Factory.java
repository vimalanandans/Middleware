package com.bezirk.middleware.proxy.android;

import android.content.Context;

import com.bezirk.middleware.Bezirk;

public abstract class Factory {
    public static Bezirk getInstance(Context context) {
        synchronized (Factory.class) {
            return new Proxy(context);
        }
    }
}
