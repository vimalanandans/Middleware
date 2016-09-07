package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.networking.NetworkManager;

import org.junit.*;
import org.mockito.Mockito;

import java.util.UUID;

public class CommsManagerTest {
    @org.junit.Test
    public void test() {
        NetworkManager networkManager = Mockito.mock(NetworkManager.class);
        Mockito.when(networkManager.getDeviceIp()).thenReturn("192.168.1.16");
        JmqCommsManager commsManager = new JmqCommsManager(networkManager, null, null, null);
        commsManager.startComms();


        new Thread(new Test.TestJp2p()).start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commsManager.stopComms();
    }
}
