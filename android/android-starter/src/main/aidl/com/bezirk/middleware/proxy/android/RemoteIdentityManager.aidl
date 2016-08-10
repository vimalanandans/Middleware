package com.bezirk.middleware.proxy.android;

interface RemoteIdentityManager {
    boolean isMiddlewareUser(in String serializedAlias);
}
