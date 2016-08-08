package com.bezirk.proxy.android;

import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.middleware.proxy.android.RemoteIdentityManager;
import com.bezirk.middleware.serialization.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServerIdentityManagerAdapter extends RemoteIdentityManager.Stub {
    private static final Gson gson;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Alias.class, new InterfaceAdapter<Alias>());
        gson = gsonBuilder.create();
    }

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
