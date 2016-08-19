/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.datastorage;

/**
 * Constants that are used to define in the database
 */
public final class PersistenceConstants {
    /* List of columns */
    public static final String COLUMN_0 = "_id";
    public static final String COLUMN_1 = "pubsubregistry";
    public static final String COLUMN_2 = "sphereregistry";
    public static final String COLUMN_3 = "zirkId";
    /* Database Connection Properties */
    public static final String DB_FILE_NAME = "mw_database.sqlite";
    public static final String DB_URL_PATH = "jdbc:sqlite:";
    /* Database Version */
    public static final String DB_VERSION = "0.0.5";

    /*	DB CONSTANTS USED ONLY ON PC_SIDE FOR STORING BEZIRKSERVICEID	*/
    //public static final String DB_FILE_NAME_BEZIRK_SID = "bezirk_serviceid.sqlite";
    /*	LOCAL DIRECTORY TODO: Change it to config directory*/
    public static final String DB_LOCATION = "./";
}

