/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
