package com.bezirk.middleware.proxy.android;

import java.util.Random;

public final class ServiceRegistration {

    private ServiceRegistration() {
        //To hide public constructor
    }

    public static final String generateUniqueServiceID() {
        Random random = new Random();
        String serviceIdAsString = Base62Random(random);
        return serviceIdAsString;
    }

    private static String Base62Random(Random random) {
        long randNum = random.nextLong();
        return Base62ToString(Math.abs(randNum));
    }

    private static String Base62ToString(long value) {
        long xValue = value % 62;
        long yValue = value / 62;
        if (yValue > 0)
            return Base62ToString(yValue) + String.valueOf(ValToChar(xValue));
        else
            return String.valueOf(ValToChar(xValue));
    }

    private static char ValToChar(long value) {
        long value9 = 9;
        if (value > value9) {
            int ascii = 65 + (int) value - 10;
            int value90 = 90;
            if (ascii > value90)
                ascii += 6;
            return (char) ascii;
        } else
            return Long.toString(value).charAt(0);
    }
}
