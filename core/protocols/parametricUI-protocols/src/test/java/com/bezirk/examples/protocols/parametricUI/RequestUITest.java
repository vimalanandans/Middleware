package com.bezirk.examples.protocols.parametricUI;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the RequestUIInputValues,RequestUIMultipleChoice and RequestUIPickOne events by retrieving the properties after deserialization.
 *
 * @author AJC6KOR
 */
public class RequestUITest {


    @Test
    public void test() {

        testRequestUIInputValues();

        testRequestUIMultipleChoice();

        testRequestUIPickOne();

    }

    private void testRequestUIInputValues() {

        InputValuesStringTriplet valueTriplet = new InputValuesStringTriplet();
        valueTriplet.label = "dimension";
        valueTriplet.type = "length";
        valueTriplet.unit = "cm";

        InputValuesStringTriplet[] values = new InputValuesStringTriplet[]{valueTriplet};
        long expiration = 25;
        RequestUIinputValues inputValues = new RequestUIinputValues(values, expiration);

        String serializedInputValues = inputValues.toJSON();

        RequestUIinputValues deserializedInputValues = RequestUIinputValues.deserialize(serializedInputValues);

        assertEquals("Expiration is not equal to the set value.", expiration, deserializedInputValues.getExpiration());
        assertEquals("Lable is not equal to the set value.", valueTriplet.label, deserializedInputValues.getValues()[0].label);
        assertEquals("Type is not equal to the set value.", valueTriplet.type, deserializedInputValues.getValues()[0].type);
        assertEquals("Unit is not equal to the set value.", valueTriplet.unit, deserializedInputValues.getValues()[0].unit);

    }

    private void testRequestUIMultipleChoice() {

        String[] availableChoices = new String[]{"answer1", "answer2", "answer3"};
        long expiration = 25;
        RequestUImultipleChoice requestUIMultipleChoice = new RequestUImultipleChoice(availableChoices, expiration);

        String serializedRequestUIMultipleChoice = requestUIMultipleChoice.toJSON();

        RequestUImultipleChoice deserializedRequestUIMultipleChoice = RequestUImultipleChoice.deserialize(serializedRequestUIMultipleChoice);

        assertEquals("Expiration is not equal to the set value.", expiration, deserializedRequestUIMultipleChoice.getExpiration());
        assertTrue("AvailableChoices is not equal to the set value.", Arrays.equals(availableChoices, deserializedRequestUIMultipleChoice.getAvailableChoices()));


    }

    private void testRequestUIPickOne() {

        String intro = "Answer Choices";
        String[] availableChoices = new String[]{"answer1", "answer2", "answer3"};
        long expiration = 25;

        RequestUIpickOne requestUIPickOne = new RequestUIpickOne(intro, availableChoices, expiration);

        String serializedRequestUIPickOne = requestUIPickOne.toJSON();

        RequestUIpickOne deserializedRequestUIPickOne = RequestUIpickOne.deserialize(serializedRequestUIPickOne);

        assertEquals("Expiration is not equal to the set value.", intro, deserializedRequestUIPickOne.getIntro());
        assertEquals("Expiration is not equal to the set value.", expiration, deserializedRequestUIPickOne.getExpiration());
        assertTrue("AvailableChoices is not equal to the set value.", Arrays.equals(availableChoices, deserializedRequestUIPickOne.getAvailableChoices()));


    }

}
