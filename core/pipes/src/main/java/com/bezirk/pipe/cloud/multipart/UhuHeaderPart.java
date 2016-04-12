package com.bezirk.pipe.cloud.multipart;


public class UhuHeaderPart extends Part {
	
	// Expected header values for this part
	public static final String EXPECTEDVAL_CONTENT_TYPE = "application/json";
	public static final String EXPECTEDVAL_CONTENT_ENCODING = "binary";
	public static final String EXPECTEDVAL_CONTENT_ID = "uhu-header";
	
	public String getUhuHeader() {
		return (String) data;
	}

}
