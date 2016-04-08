/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bosch.upa.uhu.persistence;

/**
 * Constants that are used to define in the database
 */
public final class DBConstants {
	/* List of columns */
	public static final String COLUMN_0 = "_id";
	public static final String COLUMN_1 = "sadlregistry";
	public static final String COLUMN_2 = "sphereregistry";
	public static final String COLUMN_3 = "serviceId";
	/* Database Connection Properties */
	public static final String DB_FILE_NAME = "uhu_database.sqlite";
	public static final String DB_URL_PATH = "jdbc:sqlite:";
	/* Database Version */
	public static final String DB_VERSION = "0.0.3";
	
	/*	DB CONSTANSTS USED ONLY ON PC_SIDE FOR STORING UHUSERVICEID	*/
	public static final String DB_FILE_NAME_UHU_SID = "uhu_serviceid.sqlite";
	/*	LOCAL DIRECTORY TODO: Change it to config directory*/
	public static final String DB_LOCATION = "./";
}

