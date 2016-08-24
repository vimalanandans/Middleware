package com.bezirk.test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.BezirkMiddleware;

import java.util.Timer;
import java.util.TimerTask;

public class Publisher {

    private static final String PUBLISHER_ID = "Java:Publisher";

    public Publisher() {
        final Bezirk bezirk = BezirkMiddleware.registerZirk(PUBLISHER_ID);
        HouseInfoEventSet houseEvents = new HouseInfoEventSet();
        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    System.out.println(acceptedEventUpdate.toString());
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
                airQualityUpdateEvent.sender = PUBLISHER_ID;
                airQualityUpdateEvent.pollenLevel = pollenLevel++;

                bezirk.sendEvent(airQualityUpdateEvent);
            }
        }, 500, 5000);
    }
}
