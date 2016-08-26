package com.bezirk.middleware.android.proxy;

interface RemoteIdentityManager {
    boolean isMiddlewareUser(in String serializedAlias);
}
