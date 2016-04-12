package com.bezirk.pipe.cloud.multipart;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.bezirk.control.messages.pipes.CloudStreamResponse;

public interface MultiPartParser {
	
	/**
	 * Parse a Multipart http response
	 * @param headerMap Header for the whole response
	 * @param inStream Input stream containing multiple parts to be parsed
	 * @return Parsed response
	 * @throws Exception
	 */
	public CloudStreamResponse parse(Map<String,List<String>> headerMap, InputStream inStream) throws Exception;

}
