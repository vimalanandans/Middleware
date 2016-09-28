package com.bezirk.middleware.android;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.bezirk.middleware.android.proxy.RemoteIdentityManager;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.identity.IdentityManager;
import com.google.gson.Gson;

public class ClientIdentityManagerAdapter implements IdentityManager {
    private static final String TAG = ClientIdentityManagerAdapter.class.getName();
    private static final Gson gson = new Gson();

    private static RemoteIdentityManager remoteIdentityManager;
    static ServiceConnection remoteConnection = new ServiceConnection() {
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
                Log.v(TAG, "Checking if alias is for current middleware user: " + serializedAlias);

                return remoteIdentityManager.isMiddlewareUser(serializedAlias);
            } catch (RemoteException re) {
                Log.e(TAG, "Exception accessing remote identity manager, default isMiddlewareUser = false", re);
                return false;
            }
        } else {
            Log.e(TAG, "Remote identity manager disconnected, defaulting isMiddlewareUser = false");
            return false;
        }
    }
}
