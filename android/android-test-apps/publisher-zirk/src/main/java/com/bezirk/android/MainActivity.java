package com.bezirk.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bezirk.android.publisher.R;
import com.bezirk.componentManager.AppManager;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.android.BezirkMiddleware;
import com.bezirk.test.AirQualityUpdateEvent;
import com.bezirk.test.HouseInfoEventSet;
import com.bezirk.test.UpdateAcceptedEvent;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        receiverZirk();
        senderZirk();
    }


    private void receiverZirk() {
        final Bezirk bezirk = BezirkMiddleware.registerZirk(this, "Receiver Zirk");

        HouseInfoEventSet houseEvents = new HouseInfoEventSet();

        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
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
        });

        bezirk.subscribe(houseEvents);
    }

    private void senderZirk() {

        // Start Bezirk as part of the publisher
        AppManager.getAppManager().startBezirk(this,true,"Publisher");

        final Bezirk bezirk = BezirkMiddleware.registerZirk(this, "Sender Zirk");

        HouseInfoEventSet houseEvents = new HouseInfoEventSet();

        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    tv.append("\nReceived UpdateAcceptedEvent with test field: " + acceptedEventUpdate.getTestField());
                }
            }
        });

        bezirk.subscribe(houseEvents);

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
