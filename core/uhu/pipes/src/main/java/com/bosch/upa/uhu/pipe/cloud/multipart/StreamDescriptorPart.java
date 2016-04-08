package com.bosch.upa.uhu.pipe.cloud.multipart;

public class StreamDescriptorPart extends Part {
	
	// Expected header values for this part
	public static final String EXPECTEDVAL_CONTENT_TYPE = "application/json";
	public static final String EXPECTEDVAL_CONTENT_ENCODING = "binary";
	public static final String EXPECTEDVAL_CONTENT_ID = "stream";
	

	public String getStreamDescriptor() {
		return (String) data;
	}


}
