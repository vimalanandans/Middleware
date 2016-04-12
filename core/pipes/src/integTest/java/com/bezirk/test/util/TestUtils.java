package com.bezirk.test.util;

import java.io.*;

import com.google.gson.*;

public class TestUtils {
	
	public final static String URL_UHUCLOUD_LOCALHOST = "http://localhost:8080/services/uhu";

	/**
	 * Returns a JSON string in a nice human-readable format
	 * @param uglyJsonString The string to format
	 * @return String in pretty format (the change is mainly to add newlines and indentation)
	 */
	public static String prettyPrintJson(String uglyJsonString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJsonString);
		String prettyJsonString = gson.toJson(je);
		
		return prettyJsonString;
	}
	
	public static void inputStreamToOutputStream(InputStream inStream, OutputStream outStream, long offset) throws Exception {
		int next = 0;
		inStream.skip(offset);

		while ((next=inStream.read()) != -1) {
			outStream.write(next);
		}
		outStream.flush();

		if (outStream != null) {
			outStream.close();
		}
		if (inStream != null) {
			inStream.close();
		}
	}
	
}
	
