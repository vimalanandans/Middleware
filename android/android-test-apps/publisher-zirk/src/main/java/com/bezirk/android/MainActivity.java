package com.bezirk.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bezirk.android.publisher.R;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.android.BezirkMiddleware;
import com.bezirk.middleware.proxy.android.ServiceManager;
import com.bezirk.proxy.Config;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
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
        senderZirk();
    }

    public void onClearClick(View view) {
        tv.setText("");
    }

    private void senderZirk() {

        //BezirkMiddleware.initialize(this);
        //final Bezirk bezirk = BezirkMiddleware.registerZirk("Publisher Zirk");

        final Bezirk bezirk = BezirkMiddleware.registerZirk(this, "Publisher Zirk");
        HouseInfoEventSet houseEvents = new HouseInfoEventSet();

        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint) sender;

                    tv.append("\nReceived from >> " + endpoint.device +
                            " UpdateAcceptedEvent with test field: " + acceptedEventUpdate.getTestField() +
                            ", isMiddlewareUser: " +
                            bezirk.getIdentityManager().isMiddlewareUser(acceptedEventUpdate.getAlias()));
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
                //updateDisplay("Published air quality update: " + airQualityUpdateEvent.toString());
            }
        }, 0, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BezirkMiddleware.stop();
    }
}
