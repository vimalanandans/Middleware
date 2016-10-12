package com.bezirk.middleware.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.core.AirQualityUpdateEvent;
import com.bezirk.middleware.core.HouseInfoEventSet;
import com.bezirk.middleware.core.UpdateAcceptedEvent;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.android.testApp.R;
import com.jaredrummler.android.device.DeviceName;

import java.util.Timer;
import java.util.TimerTask;

public class AdvancedTestActivity extends AppCompatActivity {
    private static final String LAUNCH_MESSAGE = "For Testing inter-app/inter-platform communication.\n" +
            "Launch Publisher/Subscriber Zirk.\nIf bezirk is installed in the device, the instance is reused.\n" +
            "Else, local bezirk service is created.";
    private static final String deviceName = DeviceName.getDeviceName();
    private static final String PUBLISHER_ID = deviceName + ":AdvTest:Publisher";
    private static final String SUBSCRIBER_ID = deviceName + ":AdvTest:Subscriber";
    //private final int noOfMessageRounds = 1000; //Number of messages to be published [and get the response back for]
    private TextView messagesTextView;
    private Button publisherButton;
    private Button subscriberButton;
    private Button resetButton;
    private Button clearButton;

    private Bezirk publisherBezirk;
    private Bezirk subscriberBezirk;
    private HouseInfoEventSet houseInfoEventSetForSubscriber;
    private HouseInfoEventSet houseInfoEventSetForPublisher;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_test_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView zirkLauncherTextView = (TextView) findViewById(R.id.zirk_launcher_tv);
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
                cleanUp();
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
        //BezirkMiddleware.initialize(this);
        subscriberBezirk = BezirkMiddleware.registerZirk(SUBSCRIBER_ID);
        houseInfoEventSetForSubscriber = new HouseInfoEventSet();
        houseInfoEventSetForSubscriber.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof AirQualityUpdateEvent) {
                    AirQualityUpdateEvent aqUpdate = (AirQualityUpdateEvent) event;
                    messagesTextView.append(aqUpdate.toString() + "\n");
                    scroll();
                    subscriberBezirk.sendEvent(sender, new UpdateAcceptedEvent(SUBSCRIBER_ID,
                            "pollen level:" + aqUpdate.pollenLevel));
                }
            }
        });
        subscriberBezirk.subscribe(houseInfoEventSetForSubscriber);
    }

    private void publisherZirk() {
        //BezirkMiddleware.initialize(this);
        publisherBezirk = BezirkMiddleware.registerZirk(PUBLISHER_ID);
        houseInfoEventSetForPublisher = new HouseInfoEventSet();
        houseInfoEventSetForPublisher.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    messagesTextView.append(acceptedEventUpdate.toString() + "\n");
                    scroll();
                }
            }
        });

        publisherBezirk.subscribe(houseInfoEventSetForPublisher);

        //publish messages periodically
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int pollenLevel = 1;

            @Override
            public void run() {
                AirQualityUpdateEvent airQualityUpdateEvent = new AirQualityUpdateEvent();
                airQualityUpdateEvent.sender = PUBLISHER_ID;
                airQualityUpdateEvent.pollenLevel = pollenLevel++;

                publisherBezirk.sendEvent(airQualityUpdateEvent);
            }
        }, 0, 1500);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanUp();
    }

    private void cleanUp() {
        messagesTextView.setText("");
        buttonToggler(true);
        if (timer != null) {
            timer.cancel();
        }
        if (subscriberBezirk != null && houseInfoEventSetForSubscriber != null) {
            subscriberBezirk.unsubscribe(houseInfoEventSetForSubscriber);
        }
        if (publisherBezirk != null && houseInfoEventSetForPublisher != null) {
            publisherBezirk.unsubscribe(houseInfoEventSetForPublisher);
        }
        setTitle(R.string.title_activity_advanced_test);
    }

    private void scroll() {
        final int scrollAmount =
                messagesTextView.getLayout().getLineTop(messagesTextView.getLineCount()) -
                        messagesTextView.getHeight();
        if (scrollAmount > 0)
            messagesTextView.scrollTo(0, scrollAmount);
        else
            messagesTextView.scrollTo(0, 0);
    }
}
