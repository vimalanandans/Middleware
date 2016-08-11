package com.bezirk.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bezirk.android.publisher.R;
import com.bezirk.componentManager.AppManager;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.android.BezirkMiddleware;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.test.AirQualityUpdateEvent;
import com.bezirk.test.HouseInfoEventSet;
import com.bezirk.test.UpdateAcceptedEvent;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Button testStreaming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);

        testStreaming = (Button)findViewById(R.id.testStreamingButton);

        testStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),StreamingActivity.class);
                startActivity(intent);
            }
        });

    }

    public void OnIntegratedApp(View view) {

        tv.setText("Publisher + Subscriber Integrated with Bezirk");
        tv.setMovementMethod(new ScrollingMovementMethod());

        // Start Bezirk as part of the publisher
        AppManager.getAppManager().startBezirk(this,true,"Integrated Bezirk",null);

        senderZirk();

        receiverZirk();


    }

    public void OnStandaloneApp(View view) {
        tv.setText("Publisher running standalone");
        senderZirk();
    }


    public void onClearClick(View view) {

        tv.setText("");
        AppManager.getAppManager().stopBezirk(this);

    }

    private void receiverZirk() {
        final Bezirk bezirk = BezirkMiddleware.registerZirk(this, "Receiver Zirk");

        HouseInfoEventSet houseEvents = new HouseInfoEventSet();

        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof AirQualityUpdateEvent) {
                    AirQualityUpdateEvent aqUpdate = (AirQualityUpdateEvent) event;
                    BezirkZirkEndPoint endPoint = (BezirkZirkEndPoint)sender;

                    updateDisplay("\n"+endPoint.device+" >> Received air quality update: " + aqUpdate.toString());
                    //do something in response to this event
                    if (aqUpdate.humidity > 0.7) {
                        updateDisplay("\nHumidity is high - recommend turning on the dehumidifier.");
                        bezirk.sendEvent(sender, new UpdateAcceptedEvent("Got the value for humidity " + aqUpdate.humidity));
                    }
                    if (aqUpdate.dustLevel > 20) {
                        updateDisplay("\nDust level is high - recommend running the vacuum.");
                    }
                    if (aqUpdate.pollenLevel > 500) {
                        updateDisplay("\nPollen level is high - recommend closing the windows and running the air filter.");
                    }
                }
            }
        });

        bezirk.subscribe(houseEvents);
    }

    private void updateDisplay(String display)
    {
        display = display + tv.getText();
        tv.setText(display);

    }

    private void senderZirk() {


        final Bezirk bezirk = BezirkMiddleware.registerZirk(this, "Sender Zirk");

        HouseInfoEventSet houseEvents = new HouseInfoEventSet();

        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint)sender;

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
}
