package com.bezirk.comms;

import com.bezirk.comms.thread.JyreReceiverThread;
import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.control.messages.Ledger;
import com.bezirk.messagehandler.ServiceMessageHandler;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.pubsubbroker.BezirkSadlManager;
import com.bezirk.sphere.api.BezirkSphereForSadl;
import com.bezirk.streaming.control.Objects.StreamRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class JyreCommsManager implements BezirkComms {
    private static final Logger logger = LoggerFactory.getLogger(JyreCommsManager.class);

    MessageDispatcher msgDispatcher = null;
    BezirkSadlManager bezirkSadlManager = null;
    //Thread for Event receiver Thread
    private Thread jyreEventReceiverThread = null;

    public JyreCommsManager() {
        //this.group = group;
    }

    @Override
    public boolean initComms(CommsProperties commsProperties, InetAddress addr,
                             BezirkSadlManager bezirkSadlManager, PipeManager pipe) {

        this.bezirkSadlManager = bezirkSadlManager;
        msgDispatcher = new MessageDispatcher(bezirkSadlManager);

        //Start Receiver Threads
        jyreEventReceiverThread = new Thread(new JyreReceiverThread(null, null));

        return true;
    }

    @Override
    public boolean startComms() {
        if (jyreEventReceiverThread != null) {
            jyreEventReceiverThread.start();
        }

        return true;
    }


    @Override
    public boolean stopComms() {
        if (jyreEventReceiverThread != null)
            jyreEventReceiverThread.interrupt();

        return true;
    }

    @Override
    public boolean closeComms() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean restartComms() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean sendMessage(Ledger message) {
        JyreCommsSend commsSend = new JyreCommsSend(null);
        commsSend.sendMessage(new byte['a'], false);
        return true;
    }

    @Override
    public boolean sendStream(String uniqueKey) {
        return false;
    }

    public boolean registerStreamBook(String key, StreamRecord sRecord) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean registerNotification(
            ICommsNotification errNotificationCallback) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean registerControlMessageReceiver(Discriminator id,
                                                  ICtrlMsgReceiver receiver) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setBezirkCallback(ServiceMessageHandler bezirkCallback) {
        // TODO Auto-generated method stub

    }

    public void setSphereForSadl(BezirkSphereForSadl bezirkSphere) {
        // TODO Auto-generated method stub

    }
}
