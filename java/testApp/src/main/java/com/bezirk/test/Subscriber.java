package com.bezirk.test;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.BezirkMiddleware;

public class Subscriber {
    private static final String SUBSCRIBER_ID = "Java:Subscriber";

    public Subscriber() {
        final Bezirk bezirk = BezirkMiddleware.registerZirk(SUBSCRIBER_ID);
        HouseInfoEventSet houseEvents = new HouseInfoEventSet();
        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof AirQualityUpdateEvent) {
                    AirQualityUpdateEvent aqUpdate = (AirQualityUpdateEvent) event;
                    System.out.println(aqUpdate.toString());
                    bezirk.sendEvent(sender, new UpdateAcceptedEvent(SUBSCRIBER_ID, "pollen level:" + aqUpdate.pollenLevel));
                }
            }
        });
        bezirk.subscribe(houseEvents);
    }
}
