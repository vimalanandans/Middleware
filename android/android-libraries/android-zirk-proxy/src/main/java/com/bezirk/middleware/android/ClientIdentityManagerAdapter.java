package com.bezirk.middleware.android;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.bezirk.middleware.android.proxy.RemoteIdentityManager;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.identity.IdentityManager;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientIdentityManagerAdapter implements IdentityManager {
    private static final Logger logger = LoggerFactory.getLogger(ClientIdentityManagerAdapter.class);
    private static final Gson gson = new Gson();

    private static RemoteIdentityManager remoteIdentityManager;
    protected static ServiceConnection remoteConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            remoteIdentityManager = RemoteIdentityManager.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            remoteIdentityManager = null;
        }
    };

    @Override
    public boolean isMiddlewareUser(Alias alias) {
        if (alias == null) {
            throw new IllegalArgumentException("IdentityManager.isMiddlewareUser called with alias " +
                    "set to null");
        }

        if (remoteIdentityManager != null) {
            final String serializedAlias = gson.toJson(alias);
            try {
                logger.trace("Checking if alias is for current middleware user: " + serializedAlias);

                return remoteIdentityManager.isMiddlewareUser(serializedAlias);
            } catch (RemoteException re) {
                logger.error("Exception accessing remote identity manager, default isMiddlewareUser = false", re);
                return false;
            }
        } else {
            logger.error("Remote identity manager disconnected, defaulting isMiddlewareUser = false");
            return false;
        }
    }
}
