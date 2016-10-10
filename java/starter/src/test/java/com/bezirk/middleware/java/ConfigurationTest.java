package com.bezirk.middleware.java;


import org.junit.Test;

public class ConfigurationTest {

    private static final String PROP_KEY = "key";

    /**
     * This tests {@link Configuration#getPropertyValue(String)} method. This test needs to be run manually to check the functionality
     * <p>
     * How to test
     * <ul>
     * <li>JVM properties being retrieved fine -> In android studio goto -> Run -> Edit configurations. In VM Options section add {@code -Dkey=jvmValue}. Run the test. It should print {@code jvmValue} in console.</li>
     * <li>System properties being retrieved fine -> In android studio goto -> Run -> Edit configurations. In Environment Variable dialog add {@code name=key} and {@code value=systemValue}. Run the test. It should print {@code systemValue} in console.</li>
     * <li>If both value present, the output should be {@code jvmValue}</li>
     * </ul>
     */
    @Test
    public void test() {
        System.out.println(Configuration.getPropertyValue(PROP_KEY));
    }
}
