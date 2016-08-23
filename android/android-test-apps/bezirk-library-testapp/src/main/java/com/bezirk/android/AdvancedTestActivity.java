package com.bezirk.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bezirk.android.bezirkAsALibrary.R;
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

public class AdvancedTestActivity extends AppCompatActivity {

    private static final String LAUNCH_MESSAGE = "For Testing inter-app communication/inter-platform communication.\nLaunch Publisher/Subscriber Zirk.\nIf bezirk is installed in the device, the instance is reused.\nElse, local bezirk service is created.";
    private static final String PUBLISHER_ID = "Test:Publisher";
    private static final String SUBSCRIBER_ID = "Test:Subscriber";
    private TextView zirkLauncherTextView;
    private TextView messagesTextView;
    private Button publisherButton;
    private Button subscriberButton;
    private Button resetButton;
    private Button clearButton;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        zirkLauncherTextView = (TextView) findViewById(R.id.zirk_launcher_tv);
        zirkLauncherTextView.setText(LAUNCH_MESSAGE);

        messagesTextView = (TextView) findViewById(R.id.messages_tv);
        messagesTextView.setMovementMethod(new ScrollingMovementMethod());

        publisherButton = (Button) findViewById(R.id.publisher_launcher_button);
        subscriberButton = (Button) findViewById(R.id.subscriber_launcher_button);
        resetButton = (Button) findViewById(R.id.reset_button);
        clearButton = (Button) findViewById(R.id.clear_button);

        publisherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonToggler(false);
                publisherZirk();
                setTitle(PUBLISHER_ID);
            }
        });

        subscriberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonToggler(false);
                subscriberZirk();
                setTitle(SUBSCRIBER_ID);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesTextView.setText("");
                buttonToggler(true);
                if (timer != null) {
                    timer.cancel();
                }
                BezirkMiddleware.stop();
                setTitle(R.string.title_activity_advanced_test);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesTextView.setText("");
            }
        });
    }

    private void buttonToggler(boolean toggle) {
        subscriberButton.setEnabled(toggle);
        publisherButton.setEnabled(toggle);
        resetButton.setEnabled(!toggle);
        clearButton.setEnabled(!toggle);
    }

    private void subscriberZirk() {
        BezirkMiddleware.initialize(this);
        final Bezirk bezirk = BezirkMiddleware.registerZirk(SUBSCRIBER_ID);
        HouseInfoEventSet houseEvents = new HouseInfoEventSet();
        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof AirQualityUpdateEvent) {
                    AirQualityUpdateEvent aqUpdate = (AirQualityUpdateEvent) event;
                    messagesTextView.append(aqUpdate.toString()+"\n");
                    bezirk.sendEvent(sender, new UpdateAcceptedEvent(SUBSCRIBER_ID, "pollen level:" + aqUpdate.pollenLevel));
                }
            }
        });
        bezirk.subscribe(houseEvents);
    }

    private void publisherZirk() {
        BezirkMiddleware.initialize(this);
        final Bezirk bezirk = BezirkMiddleware.registerZirk(PUBLISHER_ID);
        HouseInfoEventSet houseEvents = new HouseInfoEventSet();
        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    messagesTextView.append(acceptedEventUpdate.toString()+"\n");
                }
            }
        });

        bezirk.subscribe(houseEvents);

        //publish messages periodically
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int pollenLevel = 1;

            @Override
            public void run() {

                AirQualityUpdateEvent airQualityUpdateEvent = new AirQualityUpdateEvent();
                airQualityUpdateEvent.sender = PUBLISHER_ID;
                airQualityUpdateEvent.pollenLevel = pollenLevel++;

                bezirk.sendEvent(airQualityUpdateEvent);
            }
        }, 0, 5000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BezirkMiddleware.stop();
    }
}
