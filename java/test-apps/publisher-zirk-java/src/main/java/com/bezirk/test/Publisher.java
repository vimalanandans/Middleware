package com.bezirk.test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.BezirkMiddleware;

import java.util.Timer;
import java.util.TimerTask;

public class Publisher {

    public static void main(String[] args) {
        final Bezirk bezirk = BezirkMiddleware.registerZirk("Publisher Zirk Java");

        HouseInfoEventSet houseEvents = new HouseInfoEventSet();

        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof UpdateAcceptedEvent) {
                    UpdateAcceptedEvent acceptedEventUpdate = (UpdateAcceptedEvent) event;
                    System.out.format("Received UpdateAcceptedEvent with test field: %s, isMiddlewareUser: %s%n%n",
                            acceptedEventUpdate.getTestField(),
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
            }
        }, 0, 5000);
    }


}
