/**
 *
 */
package com.bezirk.sphere.api;

/**
 * @author rishabh
 */
public interface DevMode {
    boolean switchMode(Mode mode);

    Mode getStatus();

    enum Mode {ON, OFF}
}
