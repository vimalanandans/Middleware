/**
 * EnvironmentObservation
 *
 * @author Cory Henson
 * @modified 10/27/2014
 */
package com.bezirk.protocols.dragonfly;

public class EnvironmentObservation extends Observation {
    /**
     * topic
     */
    public static final String topic = "environment-observation";

	/* constructors */

    public EnvironmentObservation() {
        super(topic);
    }
}
