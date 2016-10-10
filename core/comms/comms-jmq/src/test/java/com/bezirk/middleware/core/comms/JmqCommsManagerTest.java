package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.componentManager.LifeCycleObservable;
import com.bezirk.middleware.core.networking.NetworkManager;

import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class JmqCommsManagerTest {

    /**
     * Start stop JmqCommsManager 2 times and shout out
     */
    //@org.junit.Test
    public void test() {
        NetworkManager networkManager = Mockito.mock(NetworkManager.class);

        for (int i = 0; i < 2; i++) {
            LifeCycleObservable lifeCycleObservable = new LifeCycleObservable();
            JmqCommsManager commsManager = new JmqCommsManager(networkManager, null, null, null);
            lifeCycleObservable.addObserver(commsManager);
            lifeCycleObservable.transition(LifeCycleObservable.Transition.START);
            //wait for comms to finish start before sending message
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final byte[] bytes = {1};
            assertTrue(commsManager.sendToAll(bytes, true));
            lifeCycleObservable.transition(LifeCycleObservable.Transition.STOP);
            assertFalse(commsManager.sendToAll(bytes, true));
        }
    }
}
