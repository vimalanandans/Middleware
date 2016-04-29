package com.bezirk.protocols.dragonfly.test;

import com.bezirk.middleware.messages.Event;
import com.bezirk.protocols.dragonfly.UserObservation;

/**
 * This class is a sub class of the Observation Object and is used in the
 * test flow
 *
 * @author Himadri Sikhar Khargharia
 * @created 27/10/2014
 */
public class ObservationTest extends UserObservation {
    /**
     * topic
     */
    public static final String topic = "observation-test";

    /*
     * This is used for passing the number of observations in this batch of observations to create/update the particular
     * SQLLite DataSet. This is existing only in the ObservationTest Event and not a part of the Observation Event.
     *
     */
    private Integer observationsNumberInThisBatch;

    /* constructors */
    public ObservationTest() {
        super(topic);
    }

    /**
     * Use instead of the generic Message.fromJson()
     *
     * @param json
     * @return Observation
     */
    public static ObservationTest deserialize(String json) {
        return Event.fromJson(json, ObservationTest.class);
    }

    public Integer getObservationsNumberInThisBatch() {
        return observationsNumberInThisBatch;
    }

    public void setObservationsNumberInThisBatch(
            Integer observationsNumberInThisBatch) {
        this.observationsNumberInThisBatch = observationsNumberInThisBatch;
    }
}
