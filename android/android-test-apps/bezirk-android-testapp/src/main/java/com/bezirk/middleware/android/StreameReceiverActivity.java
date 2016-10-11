package com.bezirk.middleware.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.bezirk.middleware.android.testApp.R;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.core.streaming.ReceiverStreamSet;
import com.bezirk.middleware.core.streaming.StreamPublishEvent;
import com.bezirk.middleware.core.streaming.StreamReceiveEvent;
import com.bezirk.middleware.core.streaming.StreamReceiverEventSet;
import com.bezirk.middleware.core.streaming.StreamSend;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;

public class StreameReceiverActivity extends AppCompatActivity {

    private Bezirk bezirk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streame_receiver);
        //register the bezirk middleware
        BezirkMiddleware.initialize(getApplicationContext());
        bezirk = BezirkMiddleware.registerZirk("Stream_Receive_Zirk");

        //#################### Stream Set Receiver#######################//
        ReceiverStreamSet receiverStreamSet = new ReceiverStreamSet();

        receiverStreamSet.setStreamReceiver(new StreamSet.StreamReceiver() {
            @Override
            public void receiveStream(StreamDescriptor streamDescriptor, Object streamContents, ZirkEndPoint sender) {
                if (streamDescriptor.getStreamActionName().equals(StreamSend.class.getCanonicalName())) {

                    //reply to the sender StreamPublishEvent
                    Toast.makeText(getApplicationContext(), "Received file with path " +
                            streamDescriptor.getFile().getPath(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        bezirk.subscribe(receiverStreamSet);

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

        bezirk.subscribe(receiverEventSet);

    }
}
