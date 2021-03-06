/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.android.testApp.R;
import com.bezirk.middleware.core.AirQualityUpdateEvent;
import com.bezirk.middleware.core.HouseInfoEventSet;
import com.bezirk.middleware.core.UpdateAcceptedEvent;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.jaredrummler.android.device.DeviceName;

import java.util.Timer;
import java.util.TimerTask;

public class AutoTestActivity extends AppCompatActivity {
    private TextView publisherTextView;
    private TextView subscriberTextView;
    private TextView resultTextView;

    private static final String deviceName = DeviceName.getDeviceName();
    private static final String PUBLISHER_ID = deviceName + ":AutoTest:Publisher";
    private static final String SUBSCRIBER_ID = deviceName + ":AutoTest:Subscriber";
    private static final String RESULT_MESSAGE = "Test finished successfully";

    //Number of messages to be published [and get the response back for]
    private static final int NO_OF_MESSAGE_ROUNDS = 10;

    //Interval between each message being sent by the publisher
    private static final int INTERVAL = 50;

    private int noOfResponsesReceived; //number of unicast responses received by the publisher
    private Bezirk publisherBezirk;
    private Bezirk subscriberBezirk;
    private HouseInfoEventSet houseInfoEventSetForSubscriber;
    private HouseInfoEventSet houseInfoEventSetForPublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_test);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        publisherTextView = (TextView) findViewById(R.id.publisher_zirk_tv);
        publisherTextView.setMovementMethod(new ScrollingMovementMethod());

        subscriberTextView = (TextView) findViewById(R.id.subscriber_zirk_tv);
        subscriberTextView.setMovementMethod(new ScrollingMovementMethod());

        resultTextView = (TextView) findViewById(R.id.result_tv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscriberZirk();
        publisherZirk();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriberBezirk.unsubscribe(houseInfoEventSetForSubscriber);
        publisherBezirk.unsubscribe(houseInfoEventSetForPublisher);
    }

    private void subscriberZirk() {
        subscriberBezirk = BezirkMiddleware.registerZirk(SUBSCRIBER_ID);
        houseInfoEventSetForSubscriber = new HouseInfoEventSet();
        houseInfoEventSetForSubscriber.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof AirQualityUpdateEvent) {
                    AirQualityUpdateEvent aqUpdate = (AirQualityUpdateEvent) event;
                    subscriberTextView.append(aqUpdate.toString() + "\n");
                    subscriberBezirk.sendEvent(sender, new UpdateAcceptedEvent(SUBSCRIBER_ID, "pollen level:" + aqUpdate.pollenLevel));
                }
            }
        });
        subscriberBezirk.subscribe(houseInfoEventSetForSubscriber);
    }

    private void publisherZirk() {
        publisherBezirk = BezirkMiddleware.registerZirk(PUBLISHER_ID);
        houseInfoEventSetForPublisher = new HouseInfoEventSet();
        houseInfoEventSetForPublisher.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    if (acceptedEventUpdate.getSender().equalsIgnoreCase(SUBSCRIBER_ID)) {
                        publisherTextView.append(acceptedEventUpdate.toString() + "\n");
                        if (++noOfResponsesReceived == NO_OF_MESSAGE_ROUNDS) {
                            resultTextView.append(RESULT_MESSAGE);
                        }
                    }
                }
            }
        });

        publisherBezirk.subscribe(houseInfoEventSetForPublisher);

        //publish messages periodically
        new Timer().scheduleAtFixedRate(new TimerTask() {
            private int pollenLevel = 1;

            @Override
            public void run() {
                if (pollenLevel == NO_OF_MESSAGE_ROUNDS) {
                    cancel();
                }
                AirQualityUpdateEvent airQualityUpdateEvent = new AirQualityUpdateEvent();
                airQualityUpdateEvent.sender = PUBLISHER_ID;
                airQualityUpdateEvent.pollenLevel = pollenLevel++;

                publisherBezirk.sendEvent(airQualityUpdateEvent);
            }
        }, 0, INTERVAL);
    }
}
