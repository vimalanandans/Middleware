package com.bezirk.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bezirk.android.subscriber.R;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.android.BezirkMiddleware;
import com.bezirk.test.streaming.StreamPublishEvent;
import com.bezirk.test.streaming.StreamReceiveEvent;
import com.bezirk.test.streaming.StreamReceiverEventSet;

public class StreameReceiverActivity extends AppCompatActivity {

    private Bezirk bezirk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streame_receiver);
        //register the bezirk middleware
        bezirk  = BezirkMiddleware.registerZirk(getApplicationContext(), "Stream_Receive_Zirk");

        //prep the StreamReceiverEventSet and multicast to identify the receivers.
        StreamReceiverEventSet receiverEventSet = new StreamReceiverEventSet();

        receiverEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                //got a response from the receiver
                if(event instanceof StreamReceiveEvent) {

                    //reply to the sender StreamPublishEvent
                    StreamPublishEvent streamPublishEvent = new StreamPublishEvent("stream");
                    bezirk.sendEvent(sender,streamPublishEvent);
                }
            }
        });

        bezirk.subscribe(receiverEventSet);

    }
}
