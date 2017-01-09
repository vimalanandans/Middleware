package com.bezirk.middleware.android;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.android.testApp.R;
import com.bezirk.middleware.core.streaming.StreamPublishEvent;
import com.bezirk.middleware.core.streaming.StreamReceiveEvent;
import com.bezirk.middleware.core.streaming.StreamReceiverEventSet;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.StreamEvent;
import com.bezirk.middleware.streaming.Stream;
import com.jaredrummler.android.device.DeviceName;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class StreamReceiverActivity extends AppCompatActivity {

    private Bezirk bezirk;
    private static final String DEVICE_NAME = DeviceName.getDeviceName();
    private static final String SUBSCRIBER_ID = DEVICE_NAME + ":STREAM_API:Subscriber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_receiver);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //register the bezirk middleware
        bezirk = BezirkMiddleware.registerZirk(SUBSCRIBER_ID);

        //################ Discover EVENT SET ###########################//
        //prep the StreamReceiverEventSet and multicast to identify the receivers.
        final StreamReceiverEventSet receiverEventSet = new StreamReceiverEventSet();

        receiverEventSet.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof StreamReceiveEvent) {
                    final StreamPublishEvent streamPublishEvent = new StreamPublishEvent("stream");
                    bezirk.sendEvent(sender, streamPublishEvent);
                }
            }
        });
        bezirk.subscribe(receiverEventSet);

        //register for stream event receiver
        Stream.StreamEventReceiver streamEventReceiver = new Stream.StreamEventReceiver() {
            @Override
            public void receiveStreamEvent(StreamEvent event) {
                Toast.makeText(getApplicationContext(), "Status :: " + event.getStreamRecordStatus()+ "File path is "+event.getFileName() , Toast.LENGTH_SHORT).show();
            }
        };
        bezirk.subscribeToStreamReceiver(streamEventReceiver);

    }
}
