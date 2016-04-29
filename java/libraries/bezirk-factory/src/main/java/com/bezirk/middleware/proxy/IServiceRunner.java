package com.bezirk.middleware.proxy;

/**
 * Represents a bezirk zirk that can be started on the PC platform
 * ServiceRunner changed to IServiceRunner
 */
public interface IServiceRunner {

    /**
     * Called by bezirk.  Zirk developers should use this to
     * initialize any data members before starting bezirk.
     *
     * @throws Exception if there is a problem during initialization
     */
    void init() throws Exception;

    /**
     * Called by bezirk in order to run the zirk
     *
     * @throws Exception if the zirk could not be started
     */
    void run() throws Exception;

    /**
     * Used to get the absolute path to the data directory. Bezirk services
     * should read and write files to this directory.
     *
     * @return absolutePath
     */

    String getDataPath();

    /**
     * Bezirk uses this to tell the zirk where data can be written.
     * Bezirk services should read and write files to this directory.
     *
     * @param dataPath
     */
    void setDataPath(String dataPath);
}
