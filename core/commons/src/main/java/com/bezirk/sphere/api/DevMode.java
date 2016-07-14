/**
 *
 */
package com.bezirk.sphere.api;

/**
 * @author rishabh
 */
public interface DevMode {
    public boolean switchMode(Mode mode);

    public Mode getStatus();

    public enum Mode {ON, OFF}
}
