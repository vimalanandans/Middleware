package com.bezirk.pipe.cloud.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.control.messages.pipes.CloudStreamResponse;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;

public class IncrementalParser implements MultiPartParser {
	
	protected Logger log = LoggerFactory.getLogger(IncrementalParser.class);
	
	private long bytesRead = 0;

	public CloudStreamResponse parse(Map<String,List<String>> headerMap, InputStream inStream) throws Exception {
		bytesRead = 0;

		// Return the parts with this object
		CloudStreamResponse response = new CloudStreamResponse();

		// Grab boundary out of header
		String contentType = headerMap.get("Content-Type").get(0);
		String boundary = extractBoundary(contentType);
		
		// Parse the uhu pipe header part
		UhuHeaderPart uhuHeaderPart = parseUhuHeader(boundary, inStream);
		PipeMulticastHeader pipeHeader = PipeMulticastHeader.deserialize(uhuHeaderPart.getUhuHeader(), PipeMulticastHeader.class);
		if (pipeHeader == null) {
			throw new Exception("Pipe header could not be deserialized" + uhuHeaderPart.getUhuHeader());
		}
		response.setPipeHeader(pipeHeader);
		
		// Parse the <stream> (stream descriptor) part
		StreamDescriptorPart streamPart = parseStreamDescriptor(boundary, inStream);
		response.setSerializedEvent(streamPart.getStreamDescriptor());

		// Parse the <content> (stream content) part
		StreamContentPart contentPart = parseStreamContent(boundary, inStream);
		response.setStreamContent( contentPart.getContent() );
		response.setContentOffset( contentPart.getOffset() );
		response.setHttpHeader(headerMap);

		return response;
	}
	
	/**
	 * Extract boundary value from the Content-Type header value
	 * The contentType will look like:  multipart/mixed; boundary="uuid:6fdcc045-c148-42b0-82a8-7211d06b55df"
	 * In this case, the boundary to return should be:  --uuid:6fdcc045-c148-42b0-82a8-7211d06b55df
	 * 
	 * @param contentType The value of the Content-Type header
	 * @return The extracted boundary
	 */
	protected String extractBoundary(String contentType) {
		
		String[] keyVal = contentType.split("=");
		String boundary = "--" + keyVal[1].replaceAll("\"", "");

		return boundary;
	}
	
	/**
	 * 
	 * @param expectedBoundary
	 * @param inStream
	 * @param offset
	 * @return
	 * @throws Exception 
	 */
	protected StreamContentPart parseStreamContent(String expectedBoundary, InputStream inStream) throws Exception {

		StreamContentPart contentPart = new StreamContentPart();
		
		/*
		 * Parse the <content> (i.e. stream content) part in 4 steps:
		 */

		// 1. Ensure that boundary is the first line in the part
		validateBoundary(expectedBoundary, inStream, contentPart);
		log.info("Boundary validated for <content>. bytesRead: " + bytesRead);
		
		// 2. Collect headers 
		Map<String,String> partHeader = parsePartHeader(inStream, contentPart);
		log.info("Header parsed for <content>. bytesRead: " + bytesRead);
		
		// 3. Make sure header entries are valid for this part 
		validateContentHeader(partHeader);
		
		// 4. Just set the input stream and the byte offset at which the content starts
		contentPart.setData(inStream);
		contentPart.setOffset(bytesRead);
		
		return contentPart;
	}
	
	protected UhuHeaderPart parseUhuHeader(String expectedBoundary, InputStream inStream) throws Exception {
		Part streamPart = new UhuHeaderPart();
		
		/*
		 * Parse the <uhu-header> (uhuHeader) part in 4 steps:
		 */
		
		// 1. Ensure that the boundary is the first line in the part
		validateBoundary(expectedBoundary, inStream, streamPart);
		
		// 2. Collect the headers for this part
		Map<String,String> partHeader = parsePartHeader(inStream, streamPart);
		
		// 3. Make sure the header entries are valid for this part
		validateUhuHeaderHttpHeader(partHeader);
		
		// 4. We have reached the end of the HTTP header block. Now read the uhu PipeHeader
		String serializedPipeHdr = readPipeHeader(inStream, streamPart);
		
		// the header is valid and we have a serialized pipe header.  Now set the appropriate properties on the UhuHeaderPart object
		streamPart.setContentType( partHeader.get(Part.KEY_CONTENT_TYPE) );
		streamPart.setContentId( partHeader.get(Part.KEY_CONTENT_ID) );
		streamPart.setContentTransferEncoding(partHeader.get(Part.KEY_CONTENT_ENCODING) );
		streamPart.setData(serializedPipeHdr);
		
		return (UhuHeaderPart) streamPart;
	}


	/**
	 * 
	 * @param expectedBoundary
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	protected StreamDescriptorPart parseStreamDescriptor(String expectedBoundary, InputStream inStream) throws Exception {
		
		// Object used to return info collected from this part
		Part streamPart = new StreamDescriptorPart();
		
		/*
		 * Parse the <stream> (streamDescriptor) part in 4 steps:
		 */

		// 1. Ensure that boundary is the first line in the part
		validateBoundary(expectedBoundary, inStream, streamPart);
		log.info("Boundary for <stream> validated.  bytesRead: " + bytesRead);

		// 2. Collect headers 
		Map<String,String> partHeader = parsePartHeader(inStream, streamPart);
		log.info("Header for <stream> parsed. bytesread: " + bytesRead);
		
		// 3. Make sure header entries are valid for this part 
		validateStreamDescriptorHeader(partHeader);
		
		// 4. We have reached the end of the header block.  Now read the stream descriptor
		String streamDesc = readStreamDescriptor(inStream, streamPart);
		log.info("Descriptor for <stream> parsed. bytesread: " + bytesRead);
		
		// The header is valid and we have a stream descriptor, now set the appropriate properties on the StreamPart object
		streamPart.setContentType( partHeader.get(Part.KEY_CONTENT_TYPE) );
		streamPart.setContentId( partHeader.get(Part.KEY_CONTENT_ID) );
		streamPart.setContentTransferEncoding(partHeader.get(Part.KEY_CONTENT_ENCODING) );
		streamPart.setData(streamDesc);	
		
		return (StreamDescriptorPart) streamPart;
	}
	
	protected String readPipeHeader(InputStream inStream, Part part) throws IOException {
		int next = 0;
		StringBuilder stringBuilder = new StringBuilder();
		String serializedPipeHeader = null;
		
		while( (next = inStream.read()) != -1 ) {
			bytesRead++;
			System.out.print((char)next);
			
			// We found the end of a line, we have our serialized uhu header
			if (next == '\n') {
				serializedPipeHeader = stringBuilder.toString();
				log.info("Identified uhu header: " + serializedPipeHeader);
				break;
			}
			// Collect all chars except newlines
			else {
				stringBuilder.append( (char) next );
			}
		}

		return serializedPipeHeader;
	}

	
	/**
	 * 
	 * @param inStream
	 * @param bytesRead
	 * @return
	 * @throws IOException
	 */
	protected String readStreamDescriptor(InputStream inStream, Part part) throws IOException {
		int next = 0;
		StringBuilder stringBuilder = new StringBuilder();
		String streamDesc = null;
		
		while( (next = inStream.read()) != -1 ) {
			bytesRead++;
			System.out.print((char)next);
			
			// We found the end of a line, this is our stream descriptor
			if (next == '\n') {
				streamDesc = stringBuilder.toString();
				log.info("Identified streamDescriptor: " + streamDesc);
				break;
			}
			// Collect all chars except newlines
			else {
				stringBuilder.append( (char) next );
			}
		}

		return streamDesc;
	}
	
	/**
	 * 
	 * @param inStream
	 * @param bytesRead
	 * @param part
	 * @return
	 * @throws Exception
	 */
	protected Map<String,String> parsePartHeader(InputStream inStream, Part part) throws Exception {
		int next = 0;
		StringBuilder stringBuilder = new StringBuilder();
		Map<String,String> partHeader = new HashMap<String,String>();
		int lineNum = 0;
		
		while( (next = inStream.read()) != -1 ) {
			bytesRead++;
			System.out.print((char)next);
			
			// Collect all chars except newlines
			if (next != '\n') {
				stringBuilder.append( (char) next );
				continue;
			}

			// We found the end of a line, so we can grab the line and look for stuff in it
			lineNum++;
			String line = stringBuilder.toString().trim();
			log.info("FOUND HEADER LINE " + lineNum + ": <" + line + ">");
			stringBuilder = new StringBuilder();
			
			if (line.isEmpty()) {
				// empty line signifies the end of headers
				log.info("Identified empty line");
				break;
			}
			// Split headers into key/value and add to a map ... we will validate them later
			else {
				String[] keyVal = line.split(":");
				if (keyVal.length != 2) {
					throw new Exception("Did not find key:value pair");
				}
				log.info("parsed key: " + keyVal[0] + ":" + keyVal[1]);
				partHeader.put(keyVal[0].trim(), keyVal[1].trim());	
			}
		}
		
		return partHeader;
	}
	
	/**
	 * 
	 * @param expectedBoundary
	 * @param inStream
	 * @param bytesRead
	 * @param part
	 * @throws Exception
	 */
	protected void validateBoundary(String expectedBoundary, InputStream inStream, Part part) throws Exception {
		int next = 0;
		StringBuilder stringBuilder = new StringBuilder();

		while( (next = inStream.read()) != -1 ) {
			bytesRead++;
			System.out.print((char)next);
			
			// Collect all chars except newlines
			if (next != '\n') {
				stringBuilder.append( (char) next );
				continue;
			}

			// We found a line
			String line = stringBuilder.toString().trim();
			stringBuilder = new StringBuilder();

			// Check that this is the first line and it matches the expected value
			if (line.equals(expectedBoundary)) {
				log.info("Identified boundary: " + line);
				part.setBoundary(line);
				break;
			}
			else {
				throw new Exception("Expected boundary: " + expectedBoundary + " but received: " + line);
			}
		}	
	}
	
	
	/**
	 * Validate the http header for the UhuHeader message part
	 * @param httpHeader The header entries to validate
	 * @throws Exception if a header value does not match the expected type
	 */
	protected void validateUhuHeaderHttpHeader(Map<String, String> httpHeader) throws Exception {
		// Iterate through each header entry in this part
		for (String key : httpHeader.keySet()) {
			// Check that Content-Type matches the expected value
			if (key.equals(Part.KEY_CONTENT_TYPE)) {
				String contentType = httpHeader.get(key);
				if (!contentType.equals(UhuHeaderPart.EXPECTEDVAL_CONTENT_TYPE)) {
					throw new Exception("Content type <" + contentType +"> does not match expected value: " + UhuHeaderPart.EXPECTEDVAL_CONTENT_TYPE);
				}
			}
			// Check that Content-ID matches the expected value
			else if (key.equals(Part.KEY_CONTENT_ID)) {
				String contentId = httpHeader.get(key);
				if (!contentId.contains(UhuHeaderPart.EXPECTEDVAL_CONTENT_ID)) {
					throw new Exception("Content ID <" + contentId +"> does not match expected value: " + UhuHeaderPart.EXPECTEDVAL_CONTENT_ID);
				}
			}
			else if (key.equals(Part.KEY_CONTENT_ENCODING)) {
				String contentEncoding = httpHeader.get(key);
				// We probably don't care about the encoding value so just warn for now
				if (!contentEncoding.equals(UhuHeaderPart.EXPECTEDVAL_CONTENT_ENCODING)) {
					log.warn("Didn't expect to see content encoding value: " + contentEncoding);
				}
			}
			else {
				log.warn("Didn't expect to receive header key: " + key);
			}
		}
		log.info("Validated http header for UhuHeaderPart");
	}
	
	/**
	 * 
	 * @param partHeader
	 * @throws Exception
	 */
	protected void validateStreamDescriptorHeader(Map<String,String> partHeader) throws Exception {
		// Iterate through each header entry in this part
		for (String key : partHeader.keySet()) {
			// Check that Content-Type matches the expected value
			if (key.equals(Part.KEY_CONTENT_TYPE)) {
				String contentType = partHeader.get(key);
				if (!contentType.equals(StreamDescriptorPart.EXPECTEDVAL_CONTENT_TYPE)) {
					throw new Exception("Content type <" + contentType +"> does not match expected value: " + StreamDescriptorPart.EXPECTEDVAL_CONTENT_TYPE);
				}
			}
			// Check that Content-ID matches the expected value
			else if (key.equals(Part.KEY_CONTENT_ID)) {
				String contentId = partHeader.get(key);
				if (!contentId.contains(StreamDescriptorPart.EXPECTEDVAL_CONTENT_ID)) {
					throw new Exception("Content ID <" + contentId +"> does not match expected value: " + StreamDescriptorPart.EXPECTEDVAL_CONTENT_ID);
				}
			}
			else if (key.equals(Part.KEY_CONTENT_ENCODING)) {
				String contentEncoding = partHeader.get(key);
				// We provably don't care about the encoding value so just warn for now
				if (!contentEncoding.equals(StreamDescriptorPart.EXPECTEDVAL_CONTENT_ENCODING)) {
					log.warn("Didn't expect to see content encoding value: " + contentEncoding);
				}
			}
			else {
				log.warn("Didn't expect to receive header key: " + key);
			}
		}
		log.info("Validated stream part header");
	}
	
	/**
	 * 
	 * @param partHeader
	 * @throws Exception
	 */
	protected void validateContentHeader(Map<String,String> partHeader) throws Exception {
		// Iterate through each header entry in this part
		for (String key : partHeader.keySet()) {
			// Check that Content-Type matches the expected value
			if (key.equals(Part.KEY_CONTENT_TYPE)) {
				String contentType = partHeader.get(key);
				if (!contentType.equals(StreamContentPart.EXPECTEDVAL_CONTENT_TYPE)) {
					throw new Exception("Content type <" + contentType +"> does not match expected value: " + StreamContentPart.EXPECTEDVAL_CONTENT_TYPE);
				}
			}
			// Check that Content-ID matches the expected value
			else if (key.equals(Part.KEY_CONTENT_ID)) {
				String contentId = partHeader.get(key);
				if (!contentId.contains(StreamContentPart.EXPECTEDVAL_CONTENT_ID)) {
					throw new Exception("Content ID <" + contentId +"> does not match expected value: " + StreamContentPart.EXPECTEDVAL_CONTENT_ID);
				}
			}
			else if (key.equals(Part.KEY_CONTENT_ENCODING)) {
				String contentEncoding = partHeader.get(key);
				// We provably don't care about the encoding value so just warn for now
				if (!contentEncoding.equals(StreamContentPart.EXPECTEDVAL_CONTENT_ENCODING)) {
					log.warn("Didn't expect to see content encoding value: " + contentEncoding);
				}
			}
			else {
				log.warn("Didn't expect to receive header key: " + key);
			}
		}
		log.info("Validated content part header");
	}


}
