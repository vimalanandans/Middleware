package com.bezirk.pipe.cloud.multipart;

import com.bezirk.control.messages.pipes.CloudStreamResponse;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncrementalParser implements MultiPartParser {
    protected static final Logger logger = LoggerFactory.getLogger(IncrementalParser.class);

    private long bytesRead = 0;

    public CloudStreamResponse parse(Map<String, List<String>> headerMap, InputStream inStream) throws Exception {
        bytesRead = 0;

        // Return the parts with this object
        CloudStreamResponse response = new CloudStreamResponse();

        // Grab boundary out of header
        String contentType = headerMap.get("Content-Type").get(0);
        String boundary = extractBoundary(contentType);

        // Parse the bezirk pipe header part
        BezirkHeaderPart bezirkHeaderPart = parseBezirkHeader(boundary, inStream);
        PipeMulticastHeader pipeHeader = PipeMulticastHeader.deserialize(bezirkHeaderPart.getBezirkHeader(), PipeMulticastHeader.class);
        if (pipeHeader == null) {
            throw new Exception("Pipe header could not be deserialized" + bezirkHeaderPart.getBezirkHeader());
        }
        response.setPipeHeader(pipeHeader);

        // Parse the <stream> (stream descriptor) part
        StreamDescriptorPart streamPart = parseStreamDescriptor(boundary, inStream);
        response.setSerializedEvent(streamPart.getStreamDescriptor());

        // Parse the <content> (stream content) part
        StreamContentPart contentPart = parseStreamContent(boundary, inStream);
        response.setStreamContent(contentPart.getContent());
        response.setContentOffset(contentPart.getOffset());
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

        return "--" + keyVal[1].replaceAll("\"", "");
    }

    /**
     * @param expectedBoundary
     * @param inStream
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
        logger.info("Boundary validated for <content>. bytesRead: " + bytesRead);

        // 2. Collect headers
        Map<String, String> partHeader = parsePartHeader(inStream, contentPart);
        logger.info("Header parsed for <content>. bytesRead: " + bytesRead);

        // 3. Make sure header entries are valid for this part
        validateContentHeader(partHeader);

        // 4. Just set the input stream and the byte offset at which the content starts
        contentPart.setData(inStream);
        contentPart.setOffset(bytesRead);

        return contentPart;
    }

    protected BezirkHeaderPart parseBezirkHeader(String expectedBoundary, InputStream inStream) throws Exception {
        Part streamPart = new BezirkHeaderPart();

		/*
         * Parse the <bezirk-header> (uhuHeader) part in 4 steps:
		 */

        // 1. Ensure that the boundary is the first line in the part
        validateBoundary(expectedBoundary, inStream, streamPart);

        // 2. Collect the headers for this part
        Map<String, String> partHeader = parsePartHeader(inStream, streamPart);

        // 3. Make sure the header entries are valid for this part
        validateBezirkHeaderHttpHeader(partHeader);

        // 4. We have reached the end of the HTTP header block. Now read the bezirk PipeHeader
        String serializedPipeHdr = readPipeHeader(inStream, streamPart);

        // the header is valid and we have a serialized pipe header.  Now set the appropriate
        // properties on the BezirkHeaderPart object
        streamPart.setContentType(partHeader.get(Part.KEY_CONTENT_TYPE));
        streamPart.setContentId(partHeader.get(Part.KEY_CONTENT_ID));
        streamPart.setContentTransferEncoding(partHeader.get(Part.KEY_CONTENT_ENCODING));
        streamPart.setData(serializedPipeHdr);

        return (BezirkHeaderPart) streamPart;
    }


    /**
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
        logger.info("Boundary for <stream> validated.  bytesRead: {}", bytesRead);

        // 2. Collect headers
        Map<String, String> partHeader = parsePartHeader(inStream, streamPart);
        logger.info("Header for <stream> parsed. bytesRead: {}", bytesRead);

        // 3. Make sure header entries are valid for this part
        validateStreamDescriptorHeader(partHeader);

        // 4. We have reached the end of the header block.  Now read the stream descriptor
        String streamDesc = readStreamDescriptor(inStream, streamPart);
        logger.info("Descriptor for <stream> parsed. bytesRead: {}", bytesRead);

        // The header is valid and we have a stream descriptor, now set the appropriate properties on the StreamPart object
        streamPart.setContentType(partHeader.get(Part.KEY_CONTENT_TYPE));
        streamPart.setContentId(partHeader.get(Part.KEY_CONTENT_ID));
        streamPart.setContentTransferEncoding(partHeader.get(Part.KEY_CONTENT_ENCODING));
        streamPart.setData(streamDesc);

        return (StreamDescriptorPart) streamPart;
    }

    /**
     * Converts a string from the current position to the first new line character
     * to a string.
     *
     * @param is the stream whose first line will be returned as a string
     * @return the first line of the stream as a string
     * @throws IOException problem reading from <code>is</code>
     */
    private String streamLineToString(InputStream is) throws IOException {
        final StringBuilder streamData = new StringBuilder();

        int next;
        while ((next = is.read()) != -1) {
            bytesRead++;

            // We found the end of a line, this is our stream descriptor
            if (next == '\n') {
                break;
            } else { // Collect all chars except newlines
                streamData.append((char) next);
            }
        }

        return streamData.toString();
    }

    protected String readPipeHeader(InputStream inStream, Part part) throws IOException {
        final String pipeHeader = streamLineToString(inStream);
        logger.info("Identified bezirk header: {}", pipeHeader);

        return pipeHeader;
    }

    protected String readStreamDescriptor(InputStream inStream, Part part) throws IOException {
        final String streamDesc = streamLineToString(inStream);
        logger.info("Identified streamDescriptor: {}", streamDesc);

        return streamDesc;
    }

    protected Map<String, String> parsePartHeader(InputStream inStream, Part part) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        final Map<String, String> partHeader = new HashMap<String, String>();
        int lineNum = 0;

        int next;
        while ((next = inStream.read()) != -1) {
            bytesRead++;
            System.out.print((char) next);

            // Collect all chars except newlines
            if (next != '\n') {
                stringBuilder.append((char) next);
                continue;
            }

            // We found the end of a line, so we can grab the line and look for stuff in it
            lineNum++;
            String line = stringBuilder.toString().trim();
            logger.info("FOUND HEADER LINE " + lineNum + ": <" + line + ">");
            stringBuilder = new StringBuilder();

            if (line.isEmpty()) {
                // empty line signifies the end of headers
                logger.info("Identified empty line");
                break;
            }
            // Split headers into key/value and add to a map ... we will validate them later
            else {
                String[] keyVal = line.split(":");
                if (keyVal.length != 2) {
                    throw new Exception("Did not find key:value pair");
                }
                logger.info("parsed key: " + keyVal[0] + ":" + keyVal[1]);
                partHeader.put(keyVal[0].trim(), keyVal[1].trim());
            }
        }

        return partHeader;
    }

    /**
     * @param expectedBoundary
     * @param inStream
     * @param part
     * @throws Exception
     */
    protected void validateBoundary(String expectedBoundary, InputStream inStream, Part part) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();

        int next;
        while ((next = inStream.read()) != -1) {
            bytesRead++;
            System.out.print((char) next);

            // Collect all chars except newlines
            if (next != '\n') {
                stringBuilder.append((char) next);
                continue;
            }

            // We found a line
            String line = stringBuilder.toString().trim();

            // Check that this is the first line and it matches the expected value
            if (line.equals(expectedBoundary)) {
                logger.info("Identified boundary: " + line);
                part.setBoundary(line);
                break;
            } else {
                throw new Exception("Expected boundary: " + expectedBoundary + " but received: " + line);
            }
        }
    }


    /**
     * Validate the http header for the UhuHeader message part
     *
     * @param httpHeader The header entries to validate
     * @throws Exception if a header value does not match the expected type
     */
    protected void validateBezirkHeaderHttpHeader(Map<String, String> httpHeader) throws Exception {
        // Iterate through each header entry in this part
        for (Map.Entry<String, String> entry : httpHeader.entrySet()) {
            String key = entry.getKey();

            // Check that Content-Type matches the expected value
            if (key.equals(Part.KEY_CONTENT_TYPE)) {
                String contentType = entry.getValue();
                if (!contentType.equals(com.bezirk.pipe.cloud.multipart.BezirkHeaderPart.EXPECTEDVAL_CONTENT_TYPE)) {
                    throw new Exception("Content type <" + contentType + "> does not match expected value: " + com.bezirk.pipe.cloud.multipart.BezirkHeaderPart.EXPECTEDVAL_CONTENT_TYPE);
                }
            }
            // Check that Content-ID matches the expected value
            else if (key.equals(Part.KEY_CONTENT_ID)) {
                String contentId = entry.getValue();
                if (!contentId.contains(com.bezirk.pipe.cloud.multipart.BezirkHeaderPart.EXPECTEDVAL_CONTENT_ID)) {
                    throw new Exception("Content ID <" + contentId + "> does not match expected value: " + com.bezirk.pipe.cloud.multipart.BezirkHeaderPart.EXPECTEDVAL_CONTENT_ID);
                }
            } else if (key.equals(Part.KEY_CONTENT_ENCODING)) {
                String contentEncoding = entry.getValue();
                // We probably don't care about the encoding value so just warn for now
                if (!contentEncoding.equals(com.bezirk.pipe.cloud.multipart.BezirkHeaderPart.EXPECTEDVAL_CONTENT_ENCODING)) {
                    logger.warn("Didn't expect to see content encoding value: " + contentEncoding);
                }
            } else {
                logger.warn("Didn't expect to receive header key: " + key);
            }
        }
        logger.info("Validated http header for BezirkHeaderPart");
    }

    /**
     * @param partHeader
     * @throws Exception
     */
    protected void validateStreamDescriptorHeader(Map<String, String> partHeader) throws Exception {
        // Iterate through each header entry in this part
        for (Map.Entry<String, String> entry : partHeader.entrySet()) {
            String key = entry.getKey();

            // Check that Content-Type matches the expected value
            if (key.equals(Part.KEY_CONTENT_TYPE)) {
                String contentType = entry.getValue();
                if (!contentType.equals(StreamDescriptorPart.EXPECTEDVAL_CONTENT_TYPE)) {
                    throw new Exception("Content type <" + contentType + "> does not match expected value: " + StreamDescriptorPart.EXPECTEDVAL_CONTENT_TYPE);
                }
            }
            // Check that Content-ID matches the expected value
            else if (key.equals(Part.KEY_CONTENT_ID)) {
                String contentId = entry.getValue();
                if (!contentId.contains(StreamDescriptorPart.EXPECTEDVAL_CONTENT_ID)) {
                    throw new Exception("Content ID <" + contentId + "> does not match expected value: " + StreamDescriptorPart.EXPECTEDVAL_CONTENT_ID);
                }
            } else if (key.equals(Part.KEY_CONTENT_ENCODING)) {
                String contentEncoding = entry.getValue();
                // We provably don't care about the encoding value so just warn for now
                if (!contentEncoding.equals(StreamDescriptorPart.EXPECTEDVAL_CONTENT_ENCODING)) {
                    logger.warn("Didn't expect to see content encoding value: " + contentEncoding);
                }
            } else {
                logger.warn("Didn't expect to receive header key: " + key);
            }
        }
        logger.info("Validated stream part header");
    }

    /**
     * @param partHeader
     * @throws Exception
     */
    protected void validateContentHeader(Map<String, String> partHeader) throws Exception {
        // Iterate through each header entry in this part
        for (Map.Entry<String, String> entry : partHeader.entrySet()) {
            final String key = entry.getKey();

            // Check that Content-Type matches the expected value
            if (Part.KEY_CONTENT_TYPE.equals(key)) {
                String contentType = entry.getValue();
                if (!contentType.equals(StreamContentPart.EXPECTEDVAL_CONTENT_TYPE)) {
                    throw new Exception("Content type <" + contentType + "> does not match expected value: " + StreamContentPart.EXPECTEDVAL_CONTENT_TYPE);
                }
            }
            // Check that Content-ID matches the expected value
            else if (Part.KEY_CONTENT_ID.equals(key)) {
                String contentId = entry.getValue();
                if (!contentId.contains(StreamContentPart.EXPECTEDVAL_CONTENT_ID)) {
                    throw new Exception("Content ID <" + contentId + "> does not match expected value: " + StreamContentPart.EXPECTEDVAL_CONTENT_ID);
                }
            } else if (Part.KEY_CONTENT_ENCODING.equals(key)) {
                String contentEncoding = entry.getValue();
                // We provably don't care about the encoding value so just warn for now
                if (!contentEncoding.equals(StreamContentPart.EXPECTEDVAL_CONTENT_ENCODING)) {
                    logger.warn("Didn't expect to see content encoding value: {}", contentEncoding);
                }
            } else {
                logger.warn("Didn't expect to receive header key: {}", key);
            }
        }
        logger.info("Validated content part header");
    }
}
