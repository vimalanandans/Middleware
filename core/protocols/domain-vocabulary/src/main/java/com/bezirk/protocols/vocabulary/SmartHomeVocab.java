package com.bezirk.protocols.vocabulary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SmartHomeVocab {

    /**
     * data-structure defining relations between services and properties
     */
    public final static Map<String, ArrayList<String>> servicePropertyMap =
            new HashMap<String, ArrayList<String>>() {{
                // thermostat service relates to:
                put(service.THERMOSTAT, new ArrayList<String>(
                        Collections.singletonList(
                                property.CHOOSE_TEMPERATURE
                        )));

                // light service relates to:
                put(service.LIGHT, new ArrayList<String>(
                        Arrays.asList(
                                property.SET_LIGHT,
                                property.SET_HUE
                        )));

                // fan service relates to:
                put(service.FAN, new ArrayList<String>(
                        Collections.singletonList(
                                property.SET_FAN
                        )));

                // Test service relates to:
                put(service.TEST_SERVICE, new ArrayList<String>(
                        Arrays.asList(
                                property.TESTPROPERTY,
                                property.SET_TESTPROPERTY
                        )));

                put(service.COFFEE, new ArrayList<String>(
                        Arrays.asList(
                                property.CHOOSE_COFFEE_MACHINE_STATE,
                                property.CHOOSE_COFFEE_TYPE
                        )));

                put(service.CIGARETTE, new ArrayList<String>(
                        Arrays.asList(
                                property.CHOOSE_CIGARETTE_VENDING_MACHINE_STATE,
                                property.CHOOSE_CIGARETTE_BRAND
                        )));

                put(service.MUSIC_SERVICE, new ArrayList<String>(
                        Collections.singletonList(
                                property.CHOOSE_MUSIC_GENRE)));
            }};
    /**
     * data-structure defining relations between properties and services
     */
    public final static Map<String, String> propertyServiceMap =
            new HashMap<String, String>() {{
                put(property.CHOOSE_TEMPERATURE, service.THERMOSTAT);
                put(property.SET_LIGHT, service.LIGHT);
                put(property.SET_HUE, service.LIGHT);
                put(property.SET_FAN, service.FAN);
                put(property.TESTPROPERTY, service.TEST_SERVICE);
                put(property.SET_TESTPROPERTY, service.TEST_SERVICE);
                put(property.CHOOSE_CIGARETTE_VENDING_MACHINE_STATE, service.CIGARETTE);
                put(property.CHOOSE_CIGARETTE_BRAND, service.CIGARETTE);
                put(property.CHOOSE_COFFEE_MACHINE_STATE, service.COFFEE);
                put(property.CHOOSE_COFFEE_TYPE, service.COFFEE);
                put(property.CHOOSE_MUSIC_GENRE, service.MUSIC_SERVICE);

            }};

    /**
     * property [http://purl.oclc.org/NET/ssnx/ssn#Property]
     * <p/>
     * An observable Quality of an Event or Object. That is, not a quality of an abstract entity as is also
     * allowed by DUL's Quality, but rather an aspect of an entity that is intrinsic to and cannot exist
     * without the entity and is observable by a sensor.
     */
    public static class property {

        /**
         * Location
         */

        // Presence property
        // Describes the presence of a person within the environment
        // (or in some particular location)
        public static final String PRESENCE = "http://upa.bosch.com/vocab/shv#PRESENCE";

        /**
         * Thermostat
         */

        // Temperature property
        // Describes the temperature of a feature-of-interest in the environment
        // (air, water, soil, etc.)
        public static final String TEMPERATURE = "http://upa.bosch.com/vocab/shv#TEMPERATURE";

        // Set-temperature property
        // Describes the act of a user setting the temperature (on a thermostat)
        public static final String SET_TEMPERATURE = "http://upa.bosch.com/vocab/shv#SET_TEMPERATURE";

        // Set-temperature property
        // Describes the act of a user setting the temperature (on a thermostat)
        public static final String CHOOSE_TEMPERATURE = "http://upa.bosch.com/vocab/shv#CHOOSE_TEMPERATURE";

        // Set-coffee property
        // State of the coffee-machine whether (on/off)
        public static final String CHOOSE_COFFEE_MACHINE_STATE = "http://upa.bosch.com/vocab/shv#CHOOSE_COFFEE_MACHINE_STATE";

        //Set-coffee property
        // Type of coffee to be set in the coffee machine
        public static final String CHOOSE_COFFEE_TYPE = "http://upa.bosch.com/vocab/shv#CHOOSE_COFFEE_TYPE";

        // Set-CIGARETTE property
        // State of the Cigarette vending machine
        public static final String CHOOSE_CIGARETTE_VENDING_MACHINE_STATE = "http://upa.bosch.com/vocab/shv#CHOOSE_CIGARETTE_VENDING_MACHINE_STATE";

        // Set CIGARETTE Brand Property
        // Type of CIGARETTE Brand
        public static final String CHOOSE_CIGARETTE_BRAND = "http://upa.bosch.com/vocab/shv#CHOOSE_CIGARETTE_BRAND";

        //Set MUSIC GENRE Property
        //Type of Music
        public static final String CHOOSE_MUSIC_GENRE = "http://upa.bosch.com/vocab/shv#CHOOSE_MUSIC_GENRE";

        /**
         * Light
         */

        // Light-state property
        // The light-state could be represented as a boolean (on/off) or lumosity range.
        public static final String LIGHT = "http://upa.bosch.com/vocab/shv#LIGHT";

        // Light-hue property
        // Describes the light-hue in the environment (i.e., color of the light)
        public static final String HUE = "http://upa.bosch.com/vocab/shv#HUE";

        // Set-light-state property
        // Describes the act of a user setting the light-state (i.e., on a smart-bulb)
        public static final String SET_LIGHT = "http://upa.bosch.com/vocab/shv#SET_LIGHT";

        // Set-hue property
        // Describes the act of a user setting the light-hue (i.e., on a smart-bulb)
        public static final String SET_HUE = "http://upa.bosch.com/vocab/shv#SET_HUE";

        /**
         * Fan
         */

        // Fan-state property
        // The fan-state could be represented as a boolean (on/off)
        public static final String FAN = "http://upa.bosch.com/vocab/shv#FAN";

        // Set-fan property
        // Describes the act of a user setting the fan-state (i.e., with a smart-plug)
        public static final String SET_FAN = "http://upa.bosch.com/vocab/shv#SET_FAN";

        /**
         * Test
         */

        // Test property
        // Describes the testproperty of a feature-of-interest in the environment
        public static final String TESTPROPERTY = "http://upa.bosch.com/vocab/shv#TESTPROPERTY";

        // Set-TEST property
        // Describes the act of a user setting the  TestProperty
        public static final String SET_TESTPROPERTY = "http://upa.bosch.com/vocab/shv#SET_TESTPROPERTY";
    }

    /**
     * unit of measurement
     */
    public static class uom {
        // Bezirk Location
        // see: http://tahiti.si.de.bosch.com:7990/projects/UPA/repos/platform/browse/Java-Common/uhu/uhu-API/src/main/java/com/bosch/upa/uhu/api/addressing/Location.java?at=refs%2Fheads%2Ftopic%2Fplatform-0.5
        public static final String BEZIRK_LOCATION = "http://upa.bosch.com/vocab/shv#UHU_LOCATION";

        // Location Likelihood Distribution
        // see: https://fe0vmc0345.de.bosch.com/wiki/x/C4DnAQ
        public static final String LLD = "http://upa.bosch.com/vocab/shv#LLD";

        public static final String BOOLEAN = "http://upa.bosch.com/vocab/shv#BOOLEAN";
        public static final String COLOR = "http://upa.bosch.com/vocab/shv#COLOR";
        public static final String FAHRENHEIT = "http://upa.bosch.com/vocab/shv#FAHRENHEIT";
        public static final String CELCIUS = "http://upa.bosch.com/vocab/shv#CELCIUS";
        public static final String CIGARETTE_BRAND = "http://upa.bosch.com/vocab/shv#CIGARETTE_BRAND";
        public static final String MUSIC_GENRE = "http://upa.bosch.com/vocab/shv#MUSIC_GENRE";
        public static final String COFFEE_TYPE = "http://upa.bosch.com/vocab/shv#COFFEE_TYPE";
        // for TEST
        public static final String TESTUOM = "http://upa.bosch.com/vocab/shv#TESTUOM";
    }

    /**
     * services
     */
    public static class service {
        public static final String THERMOSTAT = "http://upa.bosch.com/vocab/shv#THERMOSTAT_SERVICE";
        public static final String COFFEE = "http://upa.bosch.com/vocab/shv#COFFEE_SERVICE";
        public static final String CIGARETTE = "http://upa.bosch.com/vocab/shv#CIGARETTE_SERVICE";
        public static final String LIGHT = "http://upa.bosch.com/vocab/shv#LIGHT_SERVICE";
        public static final String FAN = "http://upa.bosch.com/vocab/shv#FAN_SERVICE";
        public static final String TEST_SERVICE = "http://upa.bosch.com/vocab/shv#TEST_SERVICE";
        public static final String MUSIC_SERVICE = "http://upa.bosch.com/vocab/shv#MUSIC_SERVICE";
    }

}
