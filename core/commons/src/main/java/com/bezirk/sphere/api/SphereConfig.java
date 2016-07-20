/**
 * Interface which needs to be implemented by Java and Android versions separately for extracting and managing sphere properties
 */
package com.bezirk.sphere.api;

import com.bezirk.sphere.api.DevMode.Mode;

/**
 * @author rishabh
 */
public interface SphereConfig {

    /**
     * Initialize the relevant properties of the class like sphereId, sphereName
     * and sphereKey
     */
    void init();

    /**
     * Get the mode of operation for the sphere's, i.e. Development or
     * Production
     *
     * @return
     */
    Mode getMode();

    /**
     * @return development sphereName if operating in dev mode <br>
     * null otherwise
     */
    String getSphereName();

    /**
     * @return development sphereId if operating in dev mode <br>
     * null otherwise
     */
    String getSphereId();

    /**
     * @return development sphereKey if operating in dev mode <br>
     * null otherwise
     */
    byte[] getSphereKey();

    /**
     * Get default sphere name
     *
     * @return
     */
    String getDefaultSphereName();

    /**
     * Set default sphere name in configuration file
     *
     * @return true if the defaultSphereName is changed successfully <br>
     * false otherwise
     */
    boolean setDefaultSphereName(String name);

    /**
     * Set the passed mode in configuration file
     *
     * @param mode
     * @return true if the mode was set successfully<br>
     * false otherwise
     */
    boolean setMode(Mode mode);
}
