/**
 * Context
 *
 * @author Cory Henson
 * @modified 09/16/2014
 */
package com.bezirk.protocols.context;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.protocols.context.exception.UserPreferenceException;

//import com.bosch.upa.protocols.penguin.v01.exception.UserPreferenceException;

public class Context {
    /* Properties */

    /**
     * location
     */
    private Location location = null;

    /**
     * dateTime
     */
    private String dateTime = null;

    /**
     * partOfDay
     */
    private String partOfDay = null;
	
	
	/* Constructors */

    public Context(Location _location, String _dateTime) throws UserPreferenceException {
        this.location = _location;
        if (validateDateFormat(_dateTime)) {
            this.dateTime = _dateTime;
        } else {
            throw new UserPreferenceException();
        }
    }

    public Context() {
    }

    /**
     * This method is a regular expression validation for the
     * Date Format according to ISO 8601 (http://en.wikipedia.org/wiki/ISO_8601)
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


	/* Getter and setter methods */

    public Location getLocation() {
        return this.location;
    }

    // location
    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    // dateTime
    public void setDateTime(String dateTime) throws UserPreferenceException {
        if (validateDateFormat(dateTime)) {
            this.dateTime = dateTime;
        } else {
            throw new UserPreferenceException();
        }
    }

    public String getPartOfDay() {
        return this.partOfDay;
    }

    // partOfDay
    public void setPartOfDay(String _v) {
        this.partOfDay = _v;
    }

    public String toString() {
        return getDateTime() + " @ " + getLocation();
    }
}
