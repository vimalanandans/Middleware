package com.bezirk.comms;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.streaming.control.Objects.StreamRecord;

import java.net.InetAddress;

/**
 * Added by Vimal
 * This class is created to de-couple from the rest of uhu (spheres / sadl / stream)
 * <p/>
 * Also this would help to loop back the communication of virtual spheres
 * for junit testing. this is created to replace the 'static' MessageQueueManager
 * Re-factor, extend and rename the below based on future need
 */
public interface IUhuComms {

    /**
     * start the communication
     */
    public boolean startComms();

    /**
     * stop the communication
     */
    public boolean stopComms();

    /**
     * close the communication
     */
    public boolean closeComms();

    /**
     * restart the underlying comms
     */
    public boolean restartComms();

    /**
     * send the control or event message depends of ledger type
     */
    public boolean sendMessage(Ledger message);

    /**
     * send the stream message based on unique key
     */
    public boolean sendStream(String uniqueKey);

    public boolean registerStreamBook(String key, StreamRecord sRecord);

    // refactor this to registerCommsErrorNotification // can be error warning etc
    //@Deprecated // use register Notification
    //public boolean initErrorNotificationCallback(ICommsErrorNotification errNotificationCallback);

    public boolean registerNotification(ICommsNotification notification);

    /**
     * Initialize the communications
     * creates queues, threads, sockets
     **/
    public boolean initComms(CommsProperties commsProperties, InetAddress addr,
                             UhuSadlManager sadl, PipeManager pipe);

    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, ICtrlMsgReceiver receiver);

    /**
     * Set the sphere for sadl. for late initialization
     */
    public void setSphereForSadl(final IUhuSphereForSadl uhuSphere);

}

