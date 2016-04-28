package com.bezirk.streaming.rtc;

import com.bezirk.comms.IUhuComms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Factory class for Signaling
 */
public final class SignalingFactory {

    /**
     * Priavte logger for the class
     */
    private static final Logger log = LoggerFactory.getLogger(SignalingFactory.class);

    /**
     * Singleton Object holding the implementation of {@link Signaling}
     */
    private static Object signaling = null;

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private SignalingFactory() {

    }

    /**
     * Getting the singleton instance of class implementing {@link ISignalling}
     *
     * @return Object holding signaling instance
     */
    public static Object getSignalingInstance() {
        return signaling;
    }

    /**
     * Creates a instance of the  class implementing {@link ISignalling}
     *
     * @param className name of the class
     * @param comms     parameter to be passed to the constructor
     */
    public static void createSignalingInstance(final String className, final IUhuComms comms) {
        synchronized (SignalingFactory.class) {
            try {
                if (signaling == null) {
                    signaling = getNewInstance(className, comms);
                }
            } catch (Exception ex) {
                log.error("Signalling Instance creation failed:", ex);
            }
        }
    }

    /**
     * Checks whether the class exists during runtime
     *
     * @param className
     * @return true if found
     * false otherwise
     */
    public static boolean checkClassExists(final String className) {


        return getClass(className) != null;

    }

    /**
     * Getting the UI Chat class instance for the uhu-pc
     *
     * @param className name of the class
     * @return Object instance of class implementing UI Chat
     */
    public static Object getUIChatInstance(final String className) {
        if (checkClassExists(className)) {
            Object newInstance = getNewInstance(className);
            if (newInstance instanceof RtcUIMarker) {
                return newInstance;
            }
        }
        return null;
    }

    /**
     * Getting the UI Activity class for the uhu-android
     *
     * @param className name of the class
     * @return Activity class for UI Chat
     */
    public static Class<?> getUIActivityClass(final String className) {
        return getClass(className);
    }

    /**
     * Get the class particular class of type className
     *
     * @param className name of the class
     * @return class if found
     * null otherwise
     */
    private static Class<?> getClass(final String className) {
        Class<?> cTmp = null;
        try {
            cTmp = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Invalid class name \n", e);
        }
        return cTmp;
    }

    /**
     * Get the new object instance of the class of type className
     *
     * @param className
     * @return new instance if class found and instantiation successful else null
     */
    private static Object getNewInstance(final String className) {
        Object newInstance = null;
        try {
            newInstance = getClass(className).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("New Instance creation failed. \n", e);
        }
        return newInstance;
    }

    /**
     * Get the new object instance of the class of type className with the parameter {@link IUhuComms}
     *
     * @param className name of the class need to be instantiated
     * @param comms     parameter accepted by the constructor
     * @return new Object instance of the class {@code className}
     */
    private static Object getNewInstance(final String className, final IUhuComms comms) {
        Object newInstance = null;
        try {
            Constructor<?> ctor = getClass(className).getConstructor(IUhuComms.class);
            newInstance = ctor.newInstance(new Object[]{comms});
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
            log.error("New Instance creation failed. \n", e);
        }
        return newInstance;
    }

}
