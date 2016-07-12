package com.bezirk.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bezirk.android.R;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.middleware.proxy.android.Factory;

import java.io.File;
import java.io.InputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

//import com.bezirk.examples.protocols.parametricUI.NoticeUIshowText;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        senderZirk();
    }


    private void senderZirk() {
        final Bezirk bezirk = Factory.registerZirk(this, "Sender Zirk");
        bezirk.subscribe(new HouseInfoRole(), new BezirkListener() {
            @Override
            public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    tv.append("\nReceived UpdateAcceptedEvent with test field: " + acceptedEventUpdate.getTestField());
                }
            }

            @Override
            public void receiveStream(String topic, Stream stream, short streamId, InputStream inputStream, ZirkEndPoint sender) {

            }

            @Override
            public void receiveStream(String topic, Stream stream, short streamId, File file, ZirkEndPoint sender) {

            }

            @Override
            public void streamStatus(short streamId, StreamStates status) {

            }

            @Override
            public void pipeGranted(Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut) {

            }

            @Override
            public void pipeStatus(Pipe pipe, PipeStates status) {

            }

            @Override
            public void discovered(Set<DiscoveredZirk> zirkSet) {

            }
        });
        //publish messages periodically
        new Timer().scheduleAtFixedRate(new TimerTask() {
            int pollenLevel = 1;

            @Override
            public void run() {
                AirQualityUpdateEvent airQualityUpdateEvent = new AirQualityUpdateEvent();
                airQualityUpdateEvent.humidity = 0.8;
                airQualityUpdateEvent.dustLevel = 30;
                airQualityUpdateEvent.pollenLevel = pollenLevel++;

                bezirk.sendEvent(airQualityUpdateEvent);
                //tv.append("Published air quality update: " + airQualityUpdateEvent.toString());
            }
        }, 0, 5000);


    }


}
