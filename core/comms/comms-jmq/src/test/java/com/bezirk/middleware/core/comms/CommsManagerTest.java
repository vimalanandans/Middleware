package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.componentManager.LifecycleManager;
import com.bezirk.middleware.core.networking.NetworkManager;

import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CommsManagerTest {

    /**
     * Start stop JmqCommsManager 2 times and shout out
     */
    @org.junit.Test
    public void test() {
        NetworkManager networkManager = Mockito.mock(NetworkManager.class);

        for (int i = 0; i < 2; i++) {
            LifecycleManager lifecycleManager = new LifecycleManager();
            JmqCommsManager commsManager = new JmqCommsManager(networkManager, null, null, null);
            lifecycleManager.addObserver(commsManager);
            lifecycleManager.setState(LifecycleManager.LifecycleState.CREATED);
            lifecycleManager.setState(LifecycleManager.LifecycleState.STARTED);
            //wait for comms to finish start before sending message
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertTrue(commsManager.sendToAll("a".getBytes(), true));
            lifecycleManager.setState(LifecycleManager.LifecycleState.STOPPED);
            assertFalse(commsManager.sendToAll("a".getBytes(), true));
        }
    }
}
