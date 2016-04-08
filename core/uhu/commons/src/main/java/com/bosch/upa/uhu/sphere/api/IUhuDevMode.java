/**
 * 
 */
package com.bosch.upa.uhu.sphere.api;

/**
 * @author rishabh
 *
 */
public interface IUhuDevMode {
    public enum Mode{ON, OFF}
    
    public boolean switchMode(Mode mode);
    
    public Mode getStatus();
}
