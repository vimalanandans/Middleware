package com.bezirk.middleware.android.proxy.android;

import com.bezirk.middleware.android.proxy.RemoteIdentityManager;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.identity.IdentityManager;
import com.google.gson.Gson;

public class ServerIdentityManagerAdapter extends RemoteIdentityManager.Stub {
    private static final Gson gson = new Gson();

    private final IdentityManager identityManager;

    public ServerIdentityManagerAdapter(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    @Override
    public boolean isMiddlewareUser(String serializedAlias) {
        final Alias alias = gson.fromJson(serializedAlias, Alias.class);
        return identityManager.isMiddlewareUser(alias);
    }
}
