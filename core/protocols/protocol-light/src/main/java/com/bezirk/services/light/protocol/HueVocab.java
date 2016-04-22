package com.bezirk.services.light.protocol;

/**
 * This class is used to define all vocabulary that is pertinent to HUE
 *
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */
public class HueVocab {
    public enum Commands {ON, OFF, TOGGLE}

    public enum Color {BLUE, GREEN, YELLOW, ORANGE, PINK, RED, DEFAULT}

    /*
     * FCFS - First Come First Serve
     * KOH - King of the Hill
     */
    public enum Policy {
        FCFS, KOH
    }

    public enum BulbStatusType {STATUS, CHANGE}
}
