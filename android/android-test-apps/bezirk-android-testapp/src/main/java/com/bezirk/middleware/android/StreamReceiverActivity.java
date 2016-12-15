package com.bezirk.middleware.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.android.testApp.R;
import com.bezirk.middleware.core.streaming.StreamPublishEvent;
import com.bezirk.middleware.core.streaming.StreamReceiveEvent;
import com.bezirk.middleware.core.streaming.StreamReceiverEventSet;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.jaredrummler.android.device.DeviceName;

public class StreamReceiverActivity extends AppCompatActivity {

    private Bezirk bezirk;
    private static final String deviceName = DeviceName.getDeviceName();
    private static final String SUBSCRIBER_ID = deviceName + ":STREAM_API:Subscriber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_receiver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //register the bezirk middleware
        bezirk = BezirkMiddleware.registerZirk(SUBSCRIBER_ID);

        //################ Discover EVENT SET ###########################//
        //prep the StreamReceiverEventSet and multicast to identify the receivers.
        StreamReceiverEventSet receiverEventSet = new StreamReceiverEventSet();

        receiverEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                //got a response from the receiver
                if (event instanceof StreamReceiveEvent) {

                    //reply to the sender StreamPublishEvent
                    StreamPublishEvent streamPublishEvent = new StreamPublishEvent("stream");
                    bezirk.sendEvent(sender, streamPublishEvent);
                }
            }
        });

        //subscribe to the event set
        bezirk.subscribe(receiverEventSet);

    }
}
