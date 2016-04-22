package com.bezirk.middleware.proxy.android;

/**
 * Created by AJC6KOR on 11/19/2015.
 */
public final class StringValidatorUtil {

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private StringValidatorUtil() {

    }


    /**
     * @return true if object is not null
     */
    public static boolean isObjectNotNull(final Object object) {

        return object != null;
    }


    /**
     * Checks for Validity of String for Not null and not empty
     *
     * @param stringValues - strings to be validated
     * @return true if valid(not null & non empty), false otherwise
     */
    public static boolean areValidStrings(final String... stringValues) {

        if (stringValues == null || stringValues.length == 0) {

            return false;
        }

        for (String str : stringValues) {

            if (str == null || str.isEmpty()) {

                return false;
            }

        }

        return true;
    }

}
