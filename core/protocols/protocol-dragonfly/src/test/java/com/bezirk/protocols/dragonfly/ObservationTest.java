package com.bezirk.protocols.dragonfly;

import com.bezirk.protocols.context.exception.UserPreferenceException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This testcase verifies the Observation event by testing the date time validation, compareTo , equals and hashcode apis
 *
 * @author AJC6KOR
 */
public class ObservationTest {

    String featureOfInterest = "Light";
    String location = "FLOOR1/ROOM1/LAB";
    String observationResult = "Success";
    String observationSamplingTime = "2007-04-05 12:30-02.001+0001";
    String observedBy = "Test";
    String observedProperty = "brightness";
    double qualityOfObservation = 80.0;
    String unitOfMeasure = "test";


    @Test
    public void test() {

        testObservation();

        testDateTime();

        testEqualsAndHashCode();

        testCompareTo();

    }


    private void testEqualsAndHashCode() {

        Observation observation = new Observation();
        populateObservation(observation);
        assertTrue("Same Observation is not considered equal to itself.", observation.equals(observation));

        Observation testObservation = new Observation();
        populateObservation(testObservation);
        assertTrue("Similar Observations are considered unequal.", observation.equals(testObservation));
        assertEquals("Similar Observations have different hashcode.", observation.hashCode(), testObservation.hashCode());

		/*Quality of Observation differs*/
        testObservation.setQualityOfObservation(87.0);
        assertFalse("Different Observations are considered equal.", observation.equals(testObservation));
        assertNotEquals("Different Observations have same hashcode.", observation.hashCode(), testObservation.hashCode());

		/*Observation result differs */
        testObservation.setQualityOfObservation(80.0);
        testObservation.setObservationResult("Failure");
        assertFalse("Different Observations are considered equal.", observation.equals(testObservation));
        assertNotEquals("Different Observations have same hashcode.", observation.hashCode(), testObservation.hashCode());

		/*Both quality of observation and observation result difers */
        testObservation.setQualityOfObservation(87.0);
        testObservation.setObservationResult("Failure");
        assertFalse("Different Observations are considered equal.", observation.equals(testObservation));
        assertNotEquals("Different Observations have same hashcode.", observation.hashCode(), testObservation.hashCode());
		
		/*Observation Result is null*/
        testObservation.setObservationResult(null);
        assertFalse("Different Observations are considered equal.", observation.equals(testObservation));
        assertNotEquals("Different Observations have same hashcode.", observation.hashCode(), testObservation.hashCode());
    }


    private void testObservation() {

        Observation observation = new Observation();
        populateObservation(observation);


        String serializedObservation = observation.toJson();

        Observation deserializedObservation = Observation.deserialize(serializedObservation);

        assertEquals("FeatureOfInterest is not equal to the set value.", featureOfInterest, deserializedObservation.getFeatureOfInterest());
        assertEquals("Location is not equal to the set value.", location, deserializedObservation.getLocation());
        assertEquals("ObservationResult is not equal to the set value.", observationResult, deserializedObservation.getObservationResult());
        assertEquals("ObservationSamplingTime is not equal to the set value.", observationSamplingTime, deserializedObservation.getObservationSamplingTime());
        assertEquals("ObservedBy is not equal to the set value.", observedBy, deserializedObservation.getObservedBy());
        assertEquals("ObservedProperty is not equal to the set value.", observedProperty, deserializedObservation.getObservedProperty());
        assertEquals("UnitOfMeasure is not equal to the set value.", unitOfMeasure, deserializedObservation.getUnitOfMeasure());
        assertTrue("QualityOfObservation is not equal to the set value.", qualityOfObservation == deserializedObservation.getQualityOfObservation());
    }


    private void populateObservation(Observation observation) {
        observation.setFeatureOfInterest(featureOfInterest);
        observation.setLocation(location);
        observation.setObservationResult(observationResult);
        try {
            observation.setObservationSamplingTime(observationSamplingTime);
        } catch (UserPreferenceException e) {

            fail("Unable to set observation sampling time. " + e.getMessage());
        }
        observation.setObservedBy(observedBy);
        observation.setObservedProperty(observedProperty);
        observation.setQualityOfObservation(qualityOfObservation);
        observation.setUnitOfMeasure(unitOfMeasure);
    }


    private void testDateTime() {

        String dateTime = "2007-04 12.30-02:001+0001";
        boolean isValidDateTime = true;
        Observation observation = new UserObservation();

        try {

            observation.setObservationSamplingTime(dateTime);

        } catch (UserPreferenceException e2) {

            isValidDateTime = false;

        }

        assertFalse("Invalid date time format is allowed in Observation",
                isValidDateTime);

        dateTime = "2007-04-05 12.30-02:001+000";
        isValidDateTime = true;
        observation = new EnvironmentObservation();
        try {

            observation.setObservationSamplingTime(dateTime);

        } catch (UserPreferenceException e) {

            isValidDateTime = false;

        }

        assertFalse("Invalid date time format is allowed in Observation",
                isValidDateTime);

        dateTime = "2007-04-05";
        isValidDateTime = true;

        try {

            observation.setObservationSamplingTime(dateTime);

        } catch (UserPreferenceException e1) {

            isValidDateTime = false;

        }

        assertFalse("Invalid date time format is allowed in Observation",
                isValidDateTime);

        dateTime = "2007-04-05 12";
        isValidDateTime = true;
        try {

            observation.setObservationSamplingTime(dateTime);

        } catch (UserPreferenceException e) {

            isValidDateTime = false;

        }

        assertFalse("Invalid date time format is allowed in Observation",
                isValidDateTime);
    }


    private void testCompareTo() {

        String observationResult = "Success";
        String observationSamplingTime = "2007-04-05 12:30:02.001+0001";

        double qualityOfObservation = 80.0;

        Observation observation = new Observation();

        try {

            observation.setObservationSamplingTime(observationSamplingTime);

        } catch (UserPreferenceException e) {

            fail("Unable to set observation sampling time. " + e.getMessage());
        }
        observation.setObservationResult(observationResult);
        observation.setQualityOfObservation(qualityOfObservation);

        Observation tempObservation = new Observation();

        try {

            tempObservation.setObservationSamplingTime(observationSamplingTime);

        } catch (UserPreferenceException e) {

            fail("Unable to set observation sampling time. " + e.getMessage());
        }
        tempObservation.setObservationResult(observationResult);
        tempObservation.setQualityOfObservation(qualityOfObservation);

        assertEquals("Similar observations are considered unequal.", 0,
                observation.compareTo(tempObservation));

        tempObservation.setObservationResult(null);
        assertFalse("Different observations are considered equal.",
                observation.equals(tempObservation));

        tempObservation.setQualityOfObservation(0.0);
        assertNotEquals("Different observations are considered equal.", 0,
                observation.compareTo(tempObservation));

        observationSamplingTime = "2007-04-04 12:30:02.001+0001";

        try {
            tempObservation.setObservationSamplingTime(observationSamplingTime);

        } catch (UserPreferenceException e) {

            fail("Unable to set observation sampling time. " + e.getMessage());
        }
        assertNotEquals("Different observations are considered equal.", 0,
                observation.compareTo(tempObservation));
    }

}
