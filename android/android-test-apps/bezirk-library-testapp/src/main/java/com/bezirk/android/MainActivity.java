package com.bezirk.android;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bezirk.android.bezirkAsALibrary.R;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        OnIntegratedApp(tv);
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
        logger.debug("Inside receiverZirk......");
        HouseInfoEventSet houseEvents = new HouseInfoEventSet();

        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof AirQualityUpdateEvent) {
                    logger.debug("event instanceof AirQualityUpdateEvent");
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

        logger.debug("Inside Sender ZIRK..................");
        final Bezirk bezirk = BezirkMiddleware.registerZirk(this, "Sender Zirk");
        logger.debug("bezirk....................... ");
        HouseInfoEventSet houseEvents = new HouseInfoEventSet();
        logger.debug("houseEvents......");
        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                logger.debug("inside receiveevent of main activity");
                if (event instanceof UpdateAcceptedEvent) {
                    logger.debug("event is instance of event instanceof UpdateAcceptedEvent in MainActivity");
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
                logger.debug("inside run of main activity");
                bezirk.sendEvent(airQualityUpdateEvent);
                //updateDisplay("Published air quality update: " + airQualityUpdateEvent.toString());
            }
        }, 0, 5000);
    }
}