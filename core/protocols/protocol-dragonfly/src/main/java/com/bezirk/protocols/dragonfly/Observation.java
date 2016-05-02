/**
 * @author Cory Henson
 */
package com.bezirk.protocols.dragonfly;

import com.bezirk.middleware.messages.Event;
import com.bezirk.protocols.context.exception.UserPreferenceException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Observation extends Event implements Comparable<Observation> {
    /**
     * Logger for current class
     */
//	private final transient Logger logger = LoggerFactory
//			.getLogger(Observation.class);
    /**
     * topic
     */
    public static final String topic = "observation";

    /*
     * featureOfInterest [http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest]
     *
     * A relation between an observation and the entity whose quality was
     * observed. For example, in an observation of the weight of a person, the
     * feature of interest is the person and the quality is weight.
     */
    private String featureOfInterest = null;

    /*
     * location
     */
    private String location = null;

    /*
     * observationResult [http://purl.oclc.org/NET/ssnx/ssn#observationResult]
     *
     * Relation linking an Observation and a Result, which contains a value
     * representing the value associated with the observed Property.
     */
    private String observationResult = null;

    /*
     * observationSamplingTime
     * [http://purl.oclc.org/NET/ssnx/ssn#observationSamplingTime]
     *
     * The phenomenon time shall describe the time that the result applies to
     * the property of the feature-of-interest. This is often the time of
     * interaction by a sampling procedure or observation procedure with a
     * real-world feature.
     */
    private String observationSamplingTime = null; // Java.Date().toString

    /*
     * observedBy [http://purl.oclc.org/NET/ssnx/ssn#observedBy]
     *
     * Relation between an Observation and a Sensor.
     */
    private String observedBy = null;

    /*
     * observedProperty [http://purl.oclc.org/NET/ssnx/ssn#observedProperty]
     *
     * Relation linking an Observation to the Property that was observed. The
     * observedProperty should be a Property (hasProperty) of the
     * FeatureOfInterest (linked by featureOfInterest) of this observation.
     */
    private String observedProperty = null;

    /*
     * qualityOfObservation
     * [http://purl.oclc.org/NET/ssnx/ssn#qualityOfObservation]
     *
     * Relation linking an Observation to the adjudged quality of the result.
     * This is of course complimentary to the MeasurementCapability information
     * recorded for the Sensor that made the Observation.
     */
    private double qualityOfObservation = -1;

    /*
     * unitOfMeasure
     */
    private String unitOfMeasure = null;

	/* constructors */

    public Observation() {
        super(Flag.NOTICE, topic);
    }

    public Observation(String _topic) {
        super(Flag.NOTICE, _topic);
    }

	/* getters and setters */

    /**
     * Use instead of the generic Message.fromJson()
     *
     * @param json
     * @return Observation
     */
    public static Observation deserialize(String json) {
        return Event.fromJson(json, Observation.class);
    }

    public double getQualityOfObservation() {
        return this.qualityOfObservation;
    }

    // qualityOfObservation
    public void setQualityOfObservation(double _v) {
        this.qualityOfObservation = _v;
    }

    public String getObservationSamplingTime() {
        return this.observationSamplingTime;
    }

    // observationSamplingTime
    public void setObservationSamplingTime(String _v)
            throws UserPreferenceException {
        if (validateDateFormat(_v)) {
            this.observationSamplingTime = _v;
        } else {
            throw new UserPreferenceException();
        }
    }

    public String getFeatureOfInterest() {
        return this.featureOfInterest;
    }

    // featureOfInterest
    public void setFeatureOfInterest(String _v) {
        this.featureOfInterest = _v;
    }

    public String getUnitOfMeasure() {
        return this.unitOfMeasure;
    }

    // unitOfMeasure
    public void setUnitOfMeasure(String _v) {
        this.unitOfMeasure = _v;
    }

    public String getLocation() {
        return this.location;
    }

    // location
    public void setLocation(String _v) {
        this.location = _v;
    }

    public String getObservationResult() {
        return this.observationResult;
    }

    // observationResult
    public void setObservationResult(String _v) {
        this.observationResult = _v;
    }

    public String getObservedBy() {
        return this.observedBy;
    }

    // observedBy
    public void setObservedBy(String _v) {
        this.observedBy = _v;
    }

    public String getObservedProperty() {
        return this.observedProperty;
    }

    // observedProperty
    public void setObservedProperty(String _v) {
        this.observedProperty = _v;
    }

    /**
     * This method is a regular expression validation for the Date Format
     * according to ISO 8601 (http://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param _dateTime {@link String}
     * @return {@link Boolean}
     */
    private boolean validateDateFormat(String _dateTime) {
        boolean checkformat;
        String[] splitedDateTime = _dateTime.split("\\s+");

        if (splitedDateTime.length < 2) {
            return false;
        }
        if (splitedDateTime[1].contains(".")) {
            String intermediate = splitedDateTime[1].replaceAll("\\.", "-");
            splitedDateTime[1] = intermediate;
        }
        if (splitedDateTime[1].contains(":")) {
            String intermediate = splitedDateTime[1].replaceAll(":", "-");
            splitedDateTime[1] = intermediate;
        }
        if (splitedDateTime[1].contains("+")) {
            String intermediate = splitedDateTime[1].replaceAll("\\+", "-");
            splitedDateTime[1] = intermediate;
        }

        if (splitedDateTime[0].matches("([0-9]{4})-([0-9]{2})-([0-9]{2})"))
            checkformat = true;
        else
            checkformat = false;

        if (checkformat) {
            if (splitedDateTime[1]
                    .matches("([0-9]{2})-([0-9]{2})-([0-9]{2})-([0-9]{3})-([0-9]{4})")) {
                return true;
            } else {
                return false;
            }
        }

        return checkformat;
    }

    @Override
    public int compareTo(Observation o) {

        // Himadri: Changes for the Date Format Issue

        Date thisDate = null;
        try {
            thisDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
                    .parse(this.getObservationSamplingTime());
        } catch (ParseException e) {
            //log.error("Error occurred in parsing the Date-Time"+e);
            e.printStackTrace();
        }

        Date oDate = null;

        try {
            oDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse(o
                    .getObservationSamplingTime());
        } catch (ParseException e) {
            //log.error("Error occurred in parsing the object's Date-Time"+ e);
            e.printStackTrace();

        }

        int compare = -thisDate.compareTo(oDate);

        if (compare == 0) {
            compare = Double.compare(getQualityOfObservation(),
                    o.getQualityOfObservation());
        }
        if (compare == 0) {
            compare = this.getObservationResult().compareTo(
                    o.getObservationResult());
        }

        return compare;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Observation &&
                Float.floatToRawIntBits((float) (getQualityOfObservation() - ((Observation) obj)
                        .getQualityOfObservation())) == 0
                && getObservationResult().equals(((Observation) obj).getObservationResult());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (int) (prime * result + getQualityOfObservation() + +((getObservationResult() != null) ? getObservationResult()
                .hashCode() : 0));
        return result;
    }
}
