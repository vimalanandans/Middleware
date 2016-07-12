package com.bezirk.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bezirk.android.subscriber.R;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.proxy.android.Factory;

import java.io.File;
import java.io.InputStream;

//import com.bezirk.examples.protocols.parametricUI.NoticeUIshowText;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        receiverZirk();
    }

    private void receiverZirk() {
        final Bezirk bezirk = Factory.registerZirk(this, "Receiver Zirk");
        bezirk.subscribe(new HouseInfoRole(), new BezirkListener() {
            @Override
            public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {
                if (event instanceof AirQualityUpdateEvent) {
                    AirQualityUpdateEvent aqUpdate = (AirQualityUpdateEvent) event;
                    tv.append("\nReceived air quality update: " + aqUpdate.toString());
                    //do something in response to this event
                    if (aqUpdate.humidity > 0.7) {
                        tv.append("\nHumidity is high - recommend turning on the dehumidifier.");
                        bezirk.sendEvent(sender, new UpdateAcceptedEvent("Got the value for humidity " + aqUpdate.humidity));
                    }
                    if (aqUpdate.dustLevel > 20) {
                        tv.append("\nDust level is high - recommend running the vacuum.");
                    }
                    if (aqUpdate.pollenLevel > 500) {
                        tv.append("\nPollen level is high - recommend closing the windows and running the air filter.");
                    }
                }
            }

            @Override
            public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, InputStream inputStream, ZirkEndPoint sender) {

            }

            @Override
            public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, File file, ZirkEndPoint sender) {

            }

            @Override
            public void streamStatus(short streamId, StreamStates status) {

            }
        });
    }
}
