package com.bosch.upa.uhu.discovery;

import com.bosch.upa.uhu.comms.ICtrlMsgReceiver;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.discovery.DiscoveryRequest;
import com.bosch.upa.uhu.control.messages.discovery.DiscoveryResponse;
import com.bosch.upa.uhu.sadl.UhuSadlManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vimal on 11/16/2015.
 *
 * this delegates the service / sadl discovery related classes
 *
 */
public class DiscoveryManager implements ICtrlMsgReceiver {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryManager.class);

    private UhuSadlManager sadlManager;

    private IUhuComms comms;

    private Thread discThread;

    public DiscoveryManager(UhuSadlManager sadlManager, IUhuComms comms)
    {
       this.sadlManager = sadlManager;
       this.comms = comms;
    }

    public boolean initDiscovery()
    {

        DiscoveryProcessor.setDiscovery(new Discovery());
        discThread = new Thread (new DiscoveryProcessor() );

        /*SphereDiscoveryProcessor.setDiscovery(new SphereDiscovery(sphereDiscHandler));
        sphereDiscThread = new Thread(new SphereDiscoveryProcessor(sphereDiscHandler, this));*/

        comms.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryRequest,this);

        comms.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryResponse,this);

        if(discThread != null)
            discThread.start();

        return true;
    }

    public boolean stopDiscovery()
    {
        //Interrupt Discovery Cleaner
        if(discThread != null)
            discThread.interrupt();
        return true;
    }
    @Override
    public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
        switch (id)
        {
            case DiscoveryRequest:
                final DiscoveryRequest req = (DiscoveryRequest) ControlMessage.deserialize(serializedMsg, DiscoveryRequest.class);
                new DiscoveryRequestHandler(sadlManager, req, comms).getDiscoveryResponse();

                break;
            case DiscoveryResponse:
                //if tcMessage.message==null deserialize, else get tcMessage.message and typecast as response
                final DiscoveryResponse response = (DiscoveryResponse) ControlMessage.deserialize(serializedMsg, DiscoveryResponse.class);
                if (DiscoveryProcessor.getDiscovery().addResponse(response)) {
                    log.debug( "Discovery Response added successfully");
                } else
                    log.debug( "Problem w adding response/invoking service listener");
                break;
            default:
                log.error("Unknown control message > "+id);
                return false;
        }
        return true;
    }
}
